package com.dnk.smart.tcp.message.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.dict.redis.RedisChannel;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class SimpleRedisPublisher implements RedisPublisher {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void publish(@NonNull RedisChannel redisChannel, @NonNull JSONObject json) {
        redisTemplate.convertAndSend(redisChannel.channel(), json.toString());
    }

    @Override
    public void publish(@NonNull RedisChannel redisChannel, @NonNull String jsonStr) {
        redisTemplate.convertAndSend(redisChannel.channel(), jsonStr);
    }

    @Override
    public void publish(@NonNull RedisChannel redisChannel, @NonNull Object object) {
        this.publish(redisChannel, JSON.toJSONString(object));
    }

}
