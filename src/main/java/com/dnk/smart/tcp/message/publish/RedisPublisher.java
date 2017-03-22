package com.dnk.smart.tcp.message.publish;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.tcp.message.dict.RedisChannel;
import lombok.NonNull;

/**
 * redis客户端发布信息
 */
public interface RedisPublisher {

    void publish(@NonNull RedisChannel redisChannel, @NonNull JSONObject json);

    void publish(@NonNull RedisChannel redisChannel, @NonNull String jsonStr);

    void publish(@NonNull RedisChannel redisChannel, @NonNull Object object);

}
