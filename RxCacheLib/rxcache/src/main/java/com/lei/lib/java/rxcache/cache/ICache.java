package com.lei.lib.java.rxcache.cache;

/**
 * 内存缓存和磁盘缓存具备的功能列表：
 * <p>
 * put：将数据保存到本地（内存或磁盘）
 * get：从本地取回数据，update表示强制返回null，这在网络请求更新数据时可以发挥作用
 * contains：判断对应键值的数据是否存在
 * remove：移除对应键值的数据
 * clear：清除所有缓存
 *
 * @author lei
 * @since 2017年8月20日
 */

public interface ICache {
    boolean put(String key, byte[] data);

    byte[] get(String key, boolean update);

    boolean contains(String key);

    boolean remove(String key);

    boolean clear();
}
