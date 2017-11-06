package com.lei.lib.java.rxcache.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;
import com.lei.lib.java.rxcache.util.LogUtil;
import com.lei.lib.java.rxcache.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存实现类
 *
 * @author Lei
 * @since 2017年8月14日
 */

public class DiskCache implements ICache {
    private DiskLruCache lruCache;

    public DiskCache(Context context, String dirName, int cacheSizeByMb) {
        if (cacheSizeByMb <= 0) throw new IllegalArgumentException("diskCacheSize must > 0.");
        try {
            lruCache = DiskLruCache.open(getDiskCacheFile(context, dirName), getAppVersion(context), 1, cacheSizeByMb * 1024 * 1024);
        } catch (IOException e) {
            LogUtil.t(e);
        }
    }

    public boolean isClosed() {
        if (lruCache == null) return true;
        return lruCache.isClosed();
    }

    /**
     * 获取文件夹地址，如果不存在，则创建
     *
     * @param context 上下文
     * @param dirName 文件名
     * @return File 文件
     */
    private File getDiskCacheFile(Context context, String dirName) {
        File cacheDir;
        try {
            cacheDir = packDiskCacheFile(context, dirName);
        } catch (NullPointerException e) {
            cacheDir = new File(context.getCacheDir().getPath() + File.separator + dirName);
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 获取文件夹地址
     *
     * @param context 上下文
     * @param dirName 文件名
     * @return File 文件
     */
    private File packDiskCacheFile(Context context, String dirName) throws NullPointerException {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + dirName);
    }

    /**
     * 获取当前应用程序的版本号。
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.t(e);
        }
        return 1;
    }

    @Override
    public boolean put(String key, byte[] data) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");

        key = Utilities.Md5(key);
        if (lruCache == null) return false;
        try {
            DiskLruCache.Editor edit = lruCache.edit(key);
            if (edit == null) {
                return false;
            }
            OutputStream sink = edit.newOutputStream(0);
            if (sink != null) {
                sink.write(data, 0, data.length);
                sink.flush();
                Utilities.closeQuietly(sink);
                edit.commit();
                LogUtil.i("DiskCache save success!");
                return true;
            }
            edit.abort();
        } catch (IOException e) {
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
        if (lruCache == null) return null;
        try {
            DiskLruCache.Editor edit = lruCache.edit(key);
            if (edit == null) {
                return null;
            }

            InputStream source = edit.newInputStream(0);

            byte[] value;
            if (source != null) {
                value = Utilities.input2byte(source);
                Utilities.closeQuietly(source);
                edit.commit();
                LogUtil.i("DiskCache get success!");
                return value;
            }
            edit.abort();
        } catch (IOException e) {
            LogUtil.t(e);
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        return get(key, false) != null;
    }

    @Override
    public boolean remove(String key) {
        Utilities.checkNullOrEmpty(key, "key is null or empty.");
        key = Utilities.Md5(key);
        if (lruCache == null) return false;
        try {
            return lruCache.remove(key);
        } catch (Exception e) {
            LogUtil.t(e);
        }
        return false;
    }

    @Override
    public boolean clear() {
        if (lruCache == null) {
            return false;
        }
        try {
            lruCache.delete();
            return true;
        } catch (Exception e) {
            LogUtil.t(e);
        }
        return false;
    }
}