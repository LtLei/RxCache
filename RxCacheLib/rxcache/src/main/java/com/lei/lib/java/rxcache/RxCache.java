package com.lei.lib.java.rxcache;

import android.app.Application;

import com.lei.lib.java.rxcache.cache.CacheManager;
import com.lei.lib.java.rxcache.converter.IConverter;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.mode.CacheMode;
import com.lei.lib.java.rxcache.util.LogUtil;
import com.lei.lib.java.rxcache.util.Utilities;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * 使用此类进行缓存的增删等操作
 *
 * @author lei
 * @since 2017年8月21日
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

        public Builder setDebug(boolean debug) {
            LogUtil.setDebug(debug);
            return this;
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

    /**
     * 设置缓存的模式
     * 提供四种模式：无缓存，仅内存，仅磁盘，和双缓存
     *
     * @param cacheMode
     * @return
     */
    public RxCache setCacheMode(CacheMode cacheMode) {
        mCacheManager = getCacheManagerBuilder().setCacheMode(cacheMode).build();
        return this;
    }

    /**
     * 可以设置磁盘缓存的文件夹名称
     *
     * @param diskDirName
     * @return
     */
    public RxCache setDiskDirName(String diskDirName) {
        Utilities.checkNullOrEmpty(diskDirName, "diskDirName is null or empty");
        mCacheManager = getCacheManagerBuilder().setDiskDirName(diskDirName).build();
        return this;
    }

    /**
     * 以MB为单位，设置磁盘缓存的大小，一般100M以内就可以
     *
     * @param diskCacheSizeByMB
     * @return
     */
    public RxCache setDiskCacheSizeByMB(int diskCacheSizeByMB) {
        if (diskCacheSizeByMB < 0) throw new IllegalArgumentException("diskCacheSize < 0.");
        mCacheManager = getCacheManagerBuilder().setDiskCacheSizeByMB(diskCacheSizeByMB).build();
        return this;
    }

    /**
     * 以MB为单位，设置内存缓存的大小，默认为可用内存大小的1/8
     *
     * @param memoryCacheSizeByMB
     * @return
     */
    public RxCache setMemoryCacheSizeByMB(int memoryCacheSizeByMB) {
        if (memoryCacheSizeByMB < 0) throw new IllegalArgumentException("memoryCacheSize < 0.");
        mCacheManager = getCacheManagerBuilder().setMemoryCacheSizeByMB(memoryCacheSizeByMB).build();
        return this;
    }

    /**
     * 设置转换器，默认会使用Gson进行转换，也可以使用序列化以及自定义的转换，只需实现IConverter这个接口
     *
     * @param converter
     * @return
     */
    public RxCache setConverter(IConverter converter) {
        mCacheManager = getCacheManagerBuilder().setConverter(Utilities.checkNotNull(converter, "converter is null.")).build();
        return this;
    }

    /**
     * 获取转换器
     *
     * @return
     */
    public IConverter getConverter() {
        return getCacheManager().getConverter();
    }

    /**
     * 获取缓存模式
     *
     * @return
     */
    public CacheMode getCacheMode() {
        return getCacheManager().getCacheMode();
    }

    /**
     * 获取磁盘缓存的大小
     *
     * @return
     */
    public int getDiskCacheSizeByMB() {
        return getCacheManager().getDiskCacheSizeByMB();
    }

    /**
     * 获取内存缓存的大小
     *
     * @return
     */
    public int getMemoryCacheSizeByMB() {
        return getCacheManager().getMemoryCacheSizeByMB();
    }

    /**
     * 获取磁盘缓存的文件夹名称
     *
     * @return
     */
    public String getDiskDirName() {
        return getCacheManager().getDiskDirName();
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

    /**
     * 写入缓存
     *
     * @param key
     * @param data
     * @param cacheTime
     * @param <T>
     * @return
     */
    public <T> Observable<Boolean> put(String key, T data, long cacheTime) {
        return getCacheManager().saveLocal(data, key, cacheTime);
    }

    /**
     * 读取缓存
     * 若使用Gson转换，需要使用此类， 传递数据的class或者type
     *
     * @param key
     * @param update
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<CacheResponse<T>> get(String key, boolean update, Class<T> clazz) {
        return getCacheManager().get(key, update, clazz);
    }

    /**
     * 读取缓存
     * 若使用Gson转换，需要使用此类， 传递数据的class或者type
     *
     * @param key
     * @param update
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<CacheResponse<T>> get(String key, boolean update, Type type) {
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
    public <T> Observable<CacheResponse<T>> get(String key, boolean update) {
        return get(key, update, null);
    }

    /**
     * 根据键值，删除缓存的数据
     *
     * @param key
     * @return
     */
    public Observable<Boolean> remove(String key) {
        return getCacheManager().remove(key);
    }
    public Observable<Boolean> remove(String... keys) {
        return getCacheManager().remove(keys);
    }

    /**
     * 清空缓存
     *
     * @return
     */
    public Observable<Boolean> clear() {
        return getCacheManager().clear();
    }
}
