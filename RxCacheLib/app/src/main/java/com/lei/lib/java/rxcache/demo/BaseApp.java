package com.lei.lib.java.rxcache.demo;

import android.app.Application;

import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.converter.GsonConverter;
import com.lei.lib.java.rxcache.converter.SerializableConverter;
import com.lei.lib.java.rxcache.mode.CacheMode;

/**
 * Created by rymyz on 2017/8/21.
 */

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        RxCache.init(this);

//        new RxCache.Builder()
//                .setDebug(true)
////                .setConverter(new SerializableConverter())
//                .setConverter(new GsonConverter())
//                .setCacheMode(CacheMode.BOTH)
//                .setDiskCacheSizeByMB(50)
//                .setDiskDirName("CacheDemo")
//                .setMemoryCacheSizeByMB(50)
//                .build();

    }
}
