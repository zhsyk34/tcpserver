package com.dnk.smart.tcp.cache;

import com.dnk.smart.dict.redis.RedisKey;

import java.util.List;

interface RedisAccessor {

    void push(String key, String value);

    void push(RedisKey redisKey, String value);

    String pop(String key);

    String pop(RedisKey redisKey);

    /**
     * unsafe in thread!!!
     */
    List<String> popAll(String key);

    List<String> popAll(RedisKey redisKey);

    void put(String key, String hashKey, String value);

    void put(RedisKey redisKey, String hashKey, String value);

    String get(String key, String hashKey);

    String get(RedisKey redisKey, String hashKey);

    long remove(String key, String hashKey);

    long remove(RedisKey redisKey, String hashKey);
}
