package com.lei.lib.java.rxcache.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lei.lib.java.rxcache.entity.RealEntity;
import com.lei.lib.java.rxcache.util.LogUtil;
import com.lei.lib.java.rxcache.util.Utilities;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 使用Gson进行数据转换
 * 因频繁转换有一定的性能问题，但是基本可以忽略
 *
 * @author lei
 * @since 2017年8月21日
 */

public class GsonConverter<T> implements IConverter<T> {
    private Gson gson;

    public GsonConverter(Gson gson) {
        Utilities.checkNotNull(gson, "gson is null.");
        this.gson = gson;
    }

    public GsonConverter() {
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
    }

    @Override
    public byte[] encode(RealEntity<T> data) {
        String dataStr = gson.toJson(data);
        return dataStr.getBytes();
    }

    @Override
    public RealEntity<T> decode(byte[] cacheData, Type type) {
        Utilities.checkNotNull(type,"type is null.");

        String dataStr = new String(cacheData);
        Type objType = type(RealEntity.class,type);
        RealEntity<T> data = gson.fromJson(dataStr, objType);
        return data;
    }

    private  ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
