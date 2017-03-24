package com.dnk.smart.tcp.cache;

import com.dnk.smart.dict.redis.RedisKey;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SimpleRedisAccessor implements RedisAccessor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void push(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public void push(RedisKey redisKey, String value) {
        this.push(redisKey.key(), value);
    }

    @Override
    public String pop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public String pop(RedisKey redisKey) {
        return this.pop(redisKey.key());
    }

    @Override
    public List<String> popAll(String key) {
        List<String> list = redisTemplate.opsForList().range(key, 0, -1);
        redisTemplate.delete(key);
        return list;
    }

    @Override
    public List<String> popAll(RedisKey redisKey) {
        return this.popAll(redisKey.key());
    }

    @Override
    public void put(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public void put(RedisKey redisKey, String hashKey, String value) {
        this.put(redisKey.key(), hashKey, value);
    }

    @Override
    public String get(String key, String hashKey) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    @Override
    public String get(RedisKey redisKey, String hashKey) {
        return this.get(redisKey.key(), hashKey);
    }

    @Override
    public long remove(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public long remove(RedisKey redisKey, String hashKey) {
        return this.remove(redisKey.key(), hashKey);
    }
}


