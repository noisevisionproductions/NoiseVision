package org.noisevisionproductions.noisevision.cache.service.base;

import java.util.concurrent.TimeUnit;

public interface CacheService<K, V> {
    void cache(K key, V value);

    void cacheWithTTL(K key, V value, long ttl, TimeUnit timeUnit);

    V get(K key);

    void invalidate(K key);

    void invalidateProjectsList();

    void invalidateAll(String pattern);
}
