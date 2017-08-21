package com.lei.lib.java.rxcache.mode;

/**
 * 缓存的模式
 *
 * @author lei
 * @since 2017年8月21日
 */

public enum CacheMode {
    /**
     * 不使用缓存，这时缓存将不起作用，也就是调用时全部返回为NULL
     */
    NONE,
    /**
     * 仅使用内存缓存
     */
    ONLY_MEMORY,
    /**
     * 仅使用磁盘缓存
     */
    ONLY_DISK,
    /**
     * 同时使用内存缓存和磁盘缓存
     */
    BOTH
}
