package com.lei.lib.java.rxcache.entity;

/**
 * Rx2.x不支持NULL，所以使用此类对返回数据包装
 */

public class CacheResponse<T> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
