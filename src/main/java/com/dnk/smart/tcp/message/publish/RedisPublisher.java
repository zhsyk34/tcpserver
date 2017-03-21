package com.dnk.smart.tcp.message.publish;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import lombok.NonNull;

public interface RedisPublisher {

    void publish(@NonNull ChannelNameEnum channelName, @NonNull JSONObject json);

    void publish(@NonNull ChannelNameEnum channelName, @NonNull String jsonStr);

    void publish(@NonNull ChannelNameEnum channelName, @NonNull Object object);

}
