package com.lei.lib.java.rxcache.converter;

import com.lei.lib.java.rxcache.entity.RealEntity;

import java.lang.reflect.Type;

/**
 * Created by rymyz on 2017/8/10.
 */

public interface IConverter<T> {
    byte[] encode(RealEntity<T> data);
    //type为实际数据的type，只有GSON解析时才需要使用
    RealEntity<T> decode(byte[] cacheData, Type type);
}
