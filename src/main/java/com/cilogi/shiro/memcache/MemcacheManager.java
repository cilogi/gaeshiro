package com.cilogi.shiro.memcache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

public class MemcacheManager implements CacheManager {
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new Memcache<K, V>(name);
    }
}
