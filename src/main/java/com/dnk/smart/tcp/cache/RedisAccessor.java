package com.dnk.smart.tcp.cache;

import com.dnk.smart.tcp.message.dict.DataKeyEnum;
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

    public void push(DataKeyEnum dataKeyEnum, String value) {
        this.push(dataKeyEnum.getKey(), value);
    }

    /**
     * @param key cache-key
     * @return first value in the queue
     */
    public String pop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public String pop(DataKeyEnum dataKeyEnum) {
        return this.pop(dataKeyEnum.getKey());
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

    public List<String> popAll(DataKeyEnum dataKeyEnum) {
        return this.popAll(dataKeyEnum.getKey());
    }

    /**
     * @param key     cache-key
     * @param hashKey map-key
     * @param value   map-value
     */
    public void put(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void put(DataKeyEnum dataKeyEnum, String hashKey, String value) {
        this.put(dataKeyEnum.getKey(), hashKey, value);
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

    public String get(DataKeyEnum dataKeyEnum, String hashKey) {
        return this.get(dataKeyEnum.getKey(), hashKey);
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

    public long remove(DataKeyEnum dataKeyEnum, String hashKey) {
        return this.remove(dataKeyEnum.getKey(), hashKey);
    }

}
