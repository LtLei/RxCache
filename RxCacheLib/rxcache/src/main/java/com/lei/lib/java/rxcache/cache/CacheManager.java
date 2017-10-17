package com.lei.lib.java.rxcache.cache;

import android.content.Context;

import com.lei.lib.java.rxcache.converter.GsonConverter;
import com.lei.lib.java.rxcache.converter.IConverter;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.entity.RealEntity;
import com.lei.lib.java.rxcache.mode.CacheMode;
import com.lei.lib.java.rxcache.util.LogUtil;
import com.lei.lib.java.rxcache.util.Utilities;

import java.lang.reflect.Type;

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
        if (cacheMode == CacheMode.BOTH || cacheMode == CacheMode.ONLY_DISK)
            return diskCacheSizeByMB;
        return 0;
    }

    public int getMemoryCacheSizeByMB() {
        if (cacheMode == CacheMode.BOTH || cacheMode == CacheMode.ONLY_MEMORY)
            return memoryCacheSizeByMB;
        return 0;
    }

    public String getDiskDirName() {
        if (cacheMode == CacheMode.BOTH || cacheMode == CacheMode.ONLY_DISK)
            return diskDirName;
        return "";
    }

    public MemoryCache getMemoryCache() {
        return mMemoryCache;
    }

    public DiskCache getDiskCache() {
        if (mDiskCache == null) return mDiskCache;
        if (mDiskCache.isClosed()) {
            mDiskCache = new DiskCache(getContext(), getDiskDirName(), getDiskCacheSizeByMB());
        }
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

    public <T> Observable<CacheResponse<T>> get(final String key, final boolean update, final Type type) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        return Observable.create(new ObservableOnSubscribe<CacheResponse<T>>() {
            @Override
            public void subscribe(ObservableEmitter<CacheResponse<T>> e) throws Exception {
                CacheResponse<T> response = new CacheResponse<>();
                if (update) {
                    remove(key);
                    e.onComplete();
                }
                T data = getDataFromCache(getMemoryCache(), key, update, type);
                if (data != null) {
                    response.setData(data);
                    LogUtil.i("data from memory.");
                    e.onNext(response);
                    e.onComplete();
                } else {
                    data = getDataFromCache(getDiskCache(), key, update, type);
                    if (data != null) {
                        response.setData(data);
                        LogUtil.i("data from disk");
                        e.onNext(response);
                        e.onComplete();
                    } else {
                        LogUtil.i("data is null.");
                        e.onNext(response);
                        e.onComplete();
                    }
                }
            }
        });

        /*Observable<CacheResponse<T>> memory = Observable.create(new ObservableOnSubscribe<CacheResponse<T>>() {
            @Override
            public void subscribe(ObservableEmitter<CacheResponse<T>> e) throws Exception {
                CacheResponse<T> response = new CacheResponse<>();
                if (update) {
                    remove(key);
                    e.onComplete();
                }

                T data = getDataFromCache(getMemoryCache(), key, update, type);
                if (data != null) {
                    response.setData(data);
                    LogUtil.i("data from memory.");
                    e.onNext(response);
//                    e.onComplete();
                } else {
                    e.onComplete();
                }
            }
        });

        Observable<CacheResponse<T>> disk = Observable.create(new ObservableOnSubscribe<CacheResponse<T>>() {
            @Override
            public void subscribe(ObservableEmitter<CacheResponse<T>> e) throws Exception {
                CacheResponse<T> response = new CacheResponse<>();

                if (update) {
                    remove(key);
                    e.onNext(response);
                    e.onComplete();
                }

                T data = getDataFromCache(getDiskCache(), key, update, type);
                if (data != null) {
                    response.setData(data);
                    LogUtil.i("data from disk");
                    e.onNext(response);
                    e.onComplete();
                } else {
                    LogUtil.i("data is null.");
                    e.onNext(response);
                    e.onComplete();
                }
            }
        });
        return Observable.concat(memory, disk);*/
    }

    private <T> void save2Memory(String key, RealEntity<T> realEntity) {
        if (getMemoryCache() != null) {
            byte[] cacheData = getConverter().encode(realEntity);
            boolean save = getMemoryCache().put(key, cacheData);
            if (save) LogUtil.i("copy data from disk to memory");
        }
    }

    private <T> T getDataFromCache(ICache cache, String key, boolean update, Type type) {
        byte[] cacheData = null;
        RealEntity<T> result = null;
        if (cache != null) {
            cacheData = cache.get(key, update);
            if (cacheData != null) {
                result = (RealEntity<T>) getConverter().decode(cacheData, type);
            }
        }

        T data = null;
        if (result != null) {
            //非永久缓存，并且缓存尚未过期，或者是永久缓存
            if (result.getCacheTime() == -1 || (result.getCacheTime() != -1 && (result.getUpdateDate() + result.getCacheTime() > System.currentTimeMillis()))) {
                data = result.getDatas();
            }
        }
        //判断一下，如果data不为空，且这是从磁盘获取的，将其同步到内存缓存中
        if (data != null && cache instanceof DiskCache) {
            save2Memory(key, result);
        }
        return data;
    }

    public Observable<Boolean> remove(String key) {
        if (getCacheMode() == CacheMode.NONE) return Observable.just(true);

        boolean result = false;
        if (getDiskCache() != null) {
            result |= getDiskCache().remove(key);
            result |= !getDiskCache().contains(key);
        }
        if (getMemoryCache() != null) {
            result |= getMemoryCache().remove(key);
            result |= !getMemoryCache().contains(key);
        }

        return Observable.just(result);
    }

    public Observable<Boolean> remove(String... keys) {
        if (getCacheMode() == CacheMode.NONE) return Observable.just(true);
        boolean result = false;
        for (int i = 0; i < keys.length; i++) {
            if (getDiskCache() != null) {
                result |= getDiskCache().remove(keys[i]);
                result |= !getDiskCache().contains(keys[i]);
            }
            if (getMemoryCache() != null) {
                result |= getMemoryCache().remove(keys[i]);
                result |= !getMemoryCache().contains(keys[i]);
            }
        }

        return Observable.just(result);
    }

    public Observable<Boolean> clear() {
        if (getCacheMode() == CacheMode.NONE) return Observable.just(true);

        boolean result = false;
        if (getDiskCache() != null) result |= getDiskCache().clear();
        if (getMemoryCache() != null) result |= getMemoryCache().clear();
        return Observable.just(result);
    }
}
