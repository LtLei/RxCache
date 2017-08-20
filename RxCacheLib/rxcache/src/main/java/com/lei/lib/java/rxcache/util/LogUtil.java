package com.lei.lib.java.rxcache.util;

import android.util.Log;

/**
 * Created by lei on 2017/8/20.
 */

public class LogUtil {
    private static boolean debug = true;
    private static String tag = "RxCache";

    public static void setDebug(boolean debug) {
        if (debug) LogUtil.debug = debug;
    }

    public static void setTag(String tag) {
        if (debug) LogUtil.tag = tag;
    }

    public static void d(String content) {
        if (debug) Log.d(tag, content);
    }

    public static void i(String content) {
        if (debug) Log.i(tag, content);
    }

    public static void e(String content) {
        if (debug) Log.e(tag, content);
    }

    public static void v(String content) {
        if (debug) Log.v(tag, content);
    }

    public static void w(String content) {
        if (debug) Log.w(tag, content);
    }

    public static void t(Throwable t) {
        if (debug) t.printStackTrace();
    }
}
