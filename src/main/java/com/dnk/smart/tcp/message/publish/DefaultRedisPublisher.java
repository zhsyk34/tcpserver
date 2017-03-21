package com.dnk.smart.tcp.message.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DefaultRedisPublisher implements RedisPublisher {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void publish(@NonNull ChannelNameEnum channelName, @NonNull JSONObject json) {
        redisTemplate.convertAndSend(channelName.channel(), json.toString());
    }

    @Override
    public void publish(@NonNull ChannelNameEnum channelName, @NonNull String jsonStr) {
        redisTemplate.convertAndSend(channelName.channel(), jsonStr);
    }

    @Override
    public void publish(@NonNull ChannelNameEnum channelName, @NonNull Object object) {
        this.publish(channelName, JSON.toJSONString(object));
    }

}
