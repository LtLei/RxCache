package com.lei.lib.java.rxcache.cache;

import android.util.LruCache;

import com.lei.lib.java.rxcache.util.Utilities;

import io.reactivex.Observable;

/**
 * 内存缓存实现类
 *
 * @author Lei
 * @since 2017年8月11日
 */

public class MemoryCache implements ICache {
    private LruCache<String, byte[]> lruCache;

    public MemoryCache(int cacheSizeByMb) {
        if (cacheSizeByMb <= 0) throw new IllegalArgumentException("memoryCacheSize must > 0.");
        lruCache = new LruCache<>(cacheSizeByMb * 1024 * 1024);
    }

    @Override
    public Observable<Boolean> put(String key, byte[] data) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");

        key = Utilities.Md5(key);
        if (lruCache == null) return Observable.just(false);
        try {
            lruCache.put(key, data);
            return Observable.just(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Observable.just(false);
    }

    @Override
    public Observable<byte[]> get(String key, boolean update) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");

        key = Utilities.Md5(key);
        if (update) {
            remove(key);
            return null;
        }

        if (lruCache == null) {
            return null;
        }
        try {
            return Observable.just(lruCache.get(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<Boolean> contains(String key) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        key = Utilities.Md5(key);
        return Observable.just(lruCache.get(key) != null);
    }

    @Override
    public Observable<Boolean> remove(String key) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        key = Utilities.Md5(key);

        if (lruCache == null) return Observable.just(false);
        try {
            return Observable.just(lruCache.remove(key) != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Observable.just(false);
    }

    @Override
    public Observable<Boolean> clear() {
        if (lruCache == null) return Observable.just(false);
        try {
            lruCache.evictAll();
            return Observable.just(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Observable.just(false);
    }
}
