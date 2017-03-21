package com.dnk.smart.kit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void push(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public String pop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    //TODO
    public List<String> popAll(String key) {
        redisTemplate.multi();

        List<String> list = redisTemplate.opsForList().range(key, 0, -1);
        redisTemplate.delete(key);

        redisTemplate.exec();

        return list;
    }

    public void put(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public String get(String key, String hashKey) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

}
