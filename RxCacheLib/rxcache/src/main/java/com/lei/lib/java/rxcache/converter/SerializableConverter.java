package com.lei.lib.java.rxcache.converter;

import com.lei.lib.java.rxcache.entity.RealEntity;
import com.lei.lib.java.rxcache.util.SerializeUtil;

import java.lang.reflect.Type;

/**
 * <p>描述：序列化对象的转换器</p>
 * 1.使用改转换器，对象&对象中的其它所有对象都必须是要实现Serializable接口（序列化）<br>
 * 优点：<br>
 * 速度快<br>
 */
public class SerializableConverter implements IConverter {
    @Override
    public byte[] encode(RealEntity data) {
        return SerializeUtil.serialize(data);
    }

    @Override
    public RealEntity decode(byte[] cacheData, Type type) {
        return (RealEntity) SerializeUtil.unserialize(cacheData);
    }
}
