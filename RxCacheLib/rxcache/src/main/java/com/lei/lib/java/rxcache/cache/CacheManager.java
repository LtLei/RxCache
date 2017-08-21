package com.lei.lib.java.rxcache.cache;

import android.content.Context;

import com.lei.lib.java.rxcache.converter.GsonConverter;
import com.lei.lib.java.rxcache.converter.IConverter;
import com.lei.lib.java.rxcache.entity.RealEntity;
import com.lei.lib.java.rxcache.mode.CacheMode;
import com.lei.lib.java.rxcache.util.Utilities;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 管理缓存
 *
 * @author lei
 * @since 2017年8月21日
 */

public class CacheManager {
    private static DiskCache mDiskCache;
    private static MemoryCache mMemoryCache;

    private Context context;
    private int memoryCacheSizeByMB;
    private int diskCacheSizeByMB;
    private IConverter converter;
    private String diskDirName;
    private CacheMode cacheMode;

    private CacheManager() {
    }

    public static class Builder {
        private Context context;
        //最大缓存的1/8
        private int memoryCacheSizeByMB = (int) (Runtime.getRuntime().maxMemory() / 8 / 1024 / 1024);
        private int diskCacheSizeByMB = 100;
        private IConverter converter = new GsonConverter();
        private String diskDirName = "RxCache";
        private CacheMode cacheMode = CacheMode.BOTH;

        public Builder(Context context) {
            this.context = Utilities.checkNotNull(context, "context is null.");
        }

        public Builder setMemoryCacheSizeByMB(int memoryCacheSizeByMB) {
            if (memoryCacheSizeByMB <= 0)
                throw new IllegalArgumentException("MemoryCacheSizeByMB < 0.");
            this.memoryCacheSizeByMB = memoryCacheSizeByMB;
            return this;
        }

        public Builder setDiskCacheSizeByMB(int diskCacheSizeByMB) {
            if (diskCacheSizeByMB <= 0)
                throw new IllegalArgumentException("DiskCacheSizeByMB < 0.");
            this.diskCacheSizeByMB = diskCacheSizeByMB;
            return this;
        }

        public Builder setDiskDirName(String diskDirName) {
            this.diskDirName = Utilities.checkNullOrEmpty(diskDirName, "diskDirName is null or empty.");
            return this;
        }

        public Builder setConverter(IConverter converter) {
            this.converter = Utilities.checkNotNull(converter, "converter is null.");
            return this;
        }

        public Builder setCacheMode(CacheMode cacheMode) {
            this.cacheMode = Utilities.checkNotNull(cacheMode, "cacheMode is null.");
            return this;
        }

        public CacheManager build() {
            CacheManager cacheManager = new CacheManager();
            cacheManager.setContext(this.context);
            cacheManager.setConverter(this.converter);
            cacheManager.setDiskCacheSizeByMB(this.diskCacheSizeByMB);
            cacheManager.setDiskDirName(this.diskDirName);
            cacheManager.setMemoryCacheSizeByMB(this.memoryCacheSizeByMB);
            cacheManager.setCacheMode(this.cacheMode);

            //初始化缓存
            switch (cacheMode) {
                case BOTH:
                    mDiskCache = new DiskCache(context, diskDirName, diskCacheSizeByMB);
                    mMemoryCache = new MemoryCache(memoryCacheSizeByMB);
                    break;
                case ONLY_DISK:
                    mDiskCache = new DiskCache(context, diskDirName, diskCacheSizeByMB);
                    mMemoryCache = null;
                    break;
                case ONLY_MEMORY:
                    mDiskCache = null;
                    mMemoryCache = new MemoryCache(memoryCacheSizeByMB);
                    break;
                case NONE:
                    mDiskCache = null;
                    mMemoryCache = null;
                    break;
            }

            return cacheManager;
        }
    }


    private void setDiskCacheSizeByMB(int diskCacheSizeByMB) {
        this.diskCacheSizeByMB = diskCacheSizeByMB;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    private void setConverter(IConverter converter) {
        this.converter = converter;
    }

    private void setDiskDirName(String diskDirName) {
        this.diskDirName = diskDirName;
    }

    private void setMemoryCacheSizeByMB(int memoryCacheSizeByMB) {
        this.memoryCacheSizeByMB = memoryCacheSizeByMB;
    }

    public Context getContext() {
        return context;
    }

    public IConverter getConverter() {
        return converter;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public int getDiskCacheSizeByMB() {
        return diskCacheSizeByMB;
    }

    public int getMemoryCacheSizeByMB() {
        return memoryCacheSizeByMB;
    }

    public String getDiskDirName() {
        return diskDirName;
    }

    public MemoryCache getMemoryCache() {
        return mMemoryCache;
    }

    public DiskCache getDiskCache() {
        return mDiskCache;
    }

    /**
     * 将数据缓存到本地的实现
     */
    public <T> Observable<Boolean> saveLocal(T data, String key, long cacheTime) {
        Utilities.checkNotNull(data, "data is null.");
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        if (cacheTime < -1) cacheTime = -1;

        RealEntity<T> entity = new RealEntity<T>(data, cacheTime);
        entity.setUpdateDate(System.currentTimeMillis());
        byte[] cacheData = getConverter().encode(entity);
        boolean result = false;
        if (getMemoryCache() != null) {
            result |= getMemoryCache().put(key, cacheData);
        }
        if (getDiskCache() != null) {
            result |= getDiskCache().put(key, cacheData);
        }

        return Observable.just(result | (getCacheMode() == CacheMode.NONE ? true : false));
    }

    public <T> Observable<T> get(String key, boolean update, final Type type) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");

        if (update) {
            remove(key);
            return (Observable<T>) handleNull();
        }

        RealEntity<T> result = null;
        //先从内存缓存中获取数据
        result = getFromCache(getMemoryCache(), key, update, type);
        if (result == null) {
            //内存缓存中不存在，从磁盘缓存中获取数据
            result = getFromCache(getDiskCache(), key, update, type);
            if (result != null) {
                //磁盘缓存中成功获取数据，将数据同步到内存缓存中
                save2Memory(key, result);

                return getData(key, result);
            }
        }

        if (result == null) {
            return (Observable<T>) handleNull();
        } else {
            return getData(key, result);
        }
    }
    private static enum Irrelevant { INSTANCE; }
    private Observable<Object> handleNull(){
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                e.onNext(Irrelevant.INSTANCE);
            }
        });
    }

    private <T> Observable<T> getData(String key, RealEntity<T> result) {
        if (result.getCacheTime() == -1) {
            //永久缓存
            return Observable.just(result.getDatas());
        }
        if (result.getUpdateDate() + result.getCacheTime() < System.currentTimeMillis()) {
            remove(key);
            return null;
        }

        return Observable.just(result.getDatas());
    }

    private <T> void save2Memory(String key, RealEntity<T> realEntity) {
        if (getMemoryCache() != null) {
            byte[] cacheData = getConverter().encode(realEntity);
            getMemoryCache().put(key, cacheData);
        }
    }

    private <T> RealEntity<T> getFromCache(ICache cache, String key, boolean update, Type type) {
        byte[] cacheData = null;
        RealEntity<T> result = null;
        if (cache != null) {
            cacheData = cache.get(key, update);
            if (cacheData != null) {
                result = (RealEntity<T>) getConverter().decode(cacheData, type);
            }
        }

        return result;
    }

    public Observable<Boolean> remove(String key) {
        boolean result = false;
        if (getDiskCache() != null) result |= getDiskCache().remove(key);
        if (getMemoryCache() != null) result |= getMemoryCache().remove(key);
        return Observable.just(result);
    }

    public Observable<Boolean> clear() {
        boolean result = false;
        if (getDiskCache() != null) result |= getDiskCache().clear();
        if (getMemoryCache() != null) result |= getMemoryCache().clear();
        return Observable.just(result);
    }
}
