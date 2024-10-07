package com.comic.server.common.repos;

import java.time.Duration;
import java.util.Optional;

/** CacheRepository */
public interface CacheRepository<K, V> {

  /**
   * Store the key value pair in cache
   *
   * @param key String key
   * @param value Integer value
   * @param ttl Duration time to live
   */
  void put(K key, V value, Duration ttl);

  /**
   * Retrieve value from cache when key is provided
   *
   * @param key String key
   * @return Optional String : value
   */
  Optional<V> get(K key);

  /**
   * Remove value from cache
   *
   * @param key String key
   * @return Boolean : true if removed successfully
   */
  Boolean remove(K key);
}
