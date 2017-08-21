package com.lei.lib.java.rxcache.cache;

import android.util.LruCache;

import com.lei.lib.java.rxcache.util.LogUtil;
import com.lei.lib.java.rxcache.util.Utilities;

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
    public boolean put(String key, byte[] data) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");

        key = Utilities.Md5(key);
        if (lruCache == null) return false;
        try {
            lruCache.put(key, data);
            LogUtil.i("MemoryCache save success!");
            return true;
        } catch (Exception e) {
            LogUtil.t(e);
        }
        return false;
    }

    @Override
    public byte[] get(String key, boolean update) {
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
            LogUtil.i("MemoryCache get success!");
            return lruCache.get(key);
        } catch (Exception e) {
            LogUtil.t(e);
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        key = Utilities.Md5(key);
        return lruCache.get(key) != null;
    }

    @Override
    public boolean remove(String key) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        key = Utilities.Md5(key);

        if (lruCache == null) return false;
        try {
            return lruCache.remove(key) != null;
        } catch (Exception e) {
            LogUtil.t(e);
        }
        return false;
    }

    @Override
    public boolean clear() {
        if (lruCache == null) return false;
        try {
            lruCache.evictAll();
            return true;
        } catch (Exception e) {
            LogUtil.t(e);
        }
        return false;
    }
}
