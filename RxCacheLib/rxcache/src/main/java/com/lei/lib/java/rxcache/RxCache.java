package com.lei.lib.java.rxcache;

import android.app.Application;

import com.lei.lib.java.rxcache.cache.CacheManager;
import com.lei.lib.java.rxcache.converter.IConverter;
import com.lei.lib.java.rxcache.mode.CacheMode;
import com.lei.lib.java.rxcache.util.Utilities;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * Created by lei on 2017/8/20.
 */

public class RxCache {
    private static Application mContext;
    private CacheManager.Builder mCacheManagerBuilder;
    private CacheManager mCacheManager;

    public static void init(Application context) {
        mContext = Utilities.checkNotNull(context, "context is null.");
    }

    private static void assertInit() {
        Utilities.checkNotNull(mContext, "context is null, you need call init() first.");
    }

    //获取实例
    private static RxCache instance = null;

    public static RxCache getInstance() {
        if (instance == null) {
            synchronized (RxCache.class) {
                if (instance == null) {
                    instance = new RxCache();
                }
            }
        }
        return instance;
    }

    //高级初始化
    public static class Builder {
        private CacheManager.Builder builder;

        public Builder() {
            assertInit();
            builder = new CacheManager.Builder(mContext);
        }

        public Builder setMemoryCacheSizeByMB(int memoryCacheSizeByMB) {
            if (memoryCacheSizeByMB <= 0)
                throw new IllegalArgumentException("MemoryCacheSizeByMB < 0.");
            builder.setMemoryCacheSizeByMB(memoryCacheSizeByMB);
            return this;
        }

        public Builder setDiskCacheSizeByMB(int diskCacheSizeByMB) {
            if (diskCacheSizeByMB <= 0)
                throw new IllegalArgumentException("DiskCacheSizeByMB < 0.");
            builder.setDiskCacheSizeByMB(diskCacheSizeByMB);
            return this;
        }

        public Builder setDiskDirName(String diskDirName) {
            builder.setDiskDirName(Utilities.checkNullOrEmpty(diskDirName, "diskDirName is null or empty."));
            return this;
        }

        public Builder setConverter(IConverter converter) {
            builder.setConverter(Utilities.checkNotNull(converter, "converter is null."));
            return this;
        }

        public Builder setCacheMode(CacheMode cacheMode) {
            builder.setCacheMode(Utilities.checkNotNull(cacheMode, "cacheMode is null."));
            return this;
        }

        public RxCache build() {
            getInstance().mCacheManagerBuilder = builder;
            return getInstance();
        }
    }

    private RxCache() {
    }

    //sets
    public RxCache setCacheMode(CacheMode cacheMode) {
        mCacheManager = getCacheManagerBuilder().setCacheMode(cacheMode).build();
        return this;
    }

    public RxCache setDiskDirName(String diskDirName) {
        Utilities.checkNullOrEmpty(diskDirName, "diskDirName is null or empty");
        mCacheManager = getCacheManagerBuilder().setDiskDirName(diskDirName).build();
        return this;
    }

    public RxCache setDiskCacheSizeByMB(int diskCacheSizeByMB) {
        if (diskCacheSizeByMB < 0) throw new IllegalArgumentException("diskCacheSize < 0.");
        mCacheManager = getCacheManagerBuilder().setDiskCacheSizeByMB(diskCacheSizeByMB).build();
        return this;
    }

    public RxCache setMemoryCacheSizeByMB(int memoryCacheSizeByMB) {
        if (memoryCacheSizeByMB < 0) throw new IllegalArgumentException("memoryCacheSize < 0.");
        mCacheManager = getCacheManagerBuilder().setMemoryCacheSizeByMB(memoryCacheSizeByMB).build();
        return this;
    }

    public RxCache setConverter(IConverter converter) {
        mCacheManager = getCacheManagerBuilder().setConverter(Utilities.checkNotNull(converter, "converter is null.")).build();
        return this;
    }

    private CacheManager.Builder getCacheManagerBuilder() {
        if (mCacheManagerBuilder == null) mCacheManagerBuilder = new CacheManager.Builder(mContext);
        return mCacheManagerBuilder;
    }

    private CacheManager getCacheManager() {
        if (mCacheManager == null) mCacheManager = getCacheManagerBuilder().build();
        return mCacheManager;
    }

    //method for use
    public <T> Observable<Boolean> put(String key, T data, long cacheTime) {
        return getCacheManager().saveLocal(data, key, cacheTime);
    }

    public <T> Observable<T> get(String key, boolean update, Class<T> clazz) {
        return getCacheManager().get(key, update, clazz);
    }

    public <T> Observable<T> get(String key, boolean update, Type type) {
        return getCacheManager().get(key, update, type);
    }

    /**
     * 如果是Gson方式，clazz传NULL可能会出错，此方法是用于序列化方式的
     *
     * @param key
     * @param update
     * @param <T>
     * @return
     */
    public <T> Observable<T> get(String key, boolean update) {
        return get(key, update, null);
    }

    public Observable<Boolean> remove(String key) {
        return getCacheManager().remove(key);
    }

    public Observable<Boolean> clear() {
        return getCacheManager().clear();
    }
}
