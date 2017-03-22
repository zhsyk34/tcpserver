package com.dnk.smart.tcp.cache;

import com.dnk.smart.tcp.cache.dict.RedisKey;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RedisAccessor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @param key   cache-key
     * @param value add value at last
     */
    public void push(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public void push(RedisKey redisKey, String value) {
        this.push(redisKey.key(), value);
    }

    /**
     * @param key cache-key
     * @return first value in the queue
     */
    public String pop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public String pop(RedisKey redisKey) {
        return this.pop(redisKey.key());
    }

    /**
     * TODO:unsafe in thread!!!
     *
     * @param key cache-key
     * @return all list
     */
    public List<String> popAll(String key) {
        redisTemplate.multi();

        List<String> list = redisTemplate.opsForList().range(key, 0, -1);
        redisTemplate.delete(key);

        redisTemplate.exec();

        return list;
    }

    public List<String> popAll(RedisKey redisKey) {
        return this.popAll(redisKey.key());
    }

    /**
     * @param key     cache-key
     * @param hashKey map-key
     * @param value   map-value
     */
    public void put(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void put(RedisKey redisKey, String hashKey, String value) {
        this.put(redisKey.key(), hashKey, value);
    }

    /**
     * @param key     cache-key
     * @param hashKey map-key
     * @return map-value
     */
    public String get(String key, String hashKey) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    public String get(RedisKey redisKey, String hashKey) {
        return this.get(redisKey.key(), hashKey);
    }

    /**
     * @param key     cache-key
     * @param hashKey map-key
     * @return delete counts
     */
    public long remove(String key, String hashKey) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        return hash.delete(key, hashKey);
    }

    public long remove(RedisKey redisKey, String hashKey) {
        return this.remove(redisKey.key(), hashKey);
    }

}
