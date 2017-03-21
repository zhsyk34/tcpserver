package com.dnk.smart.tcp.message.subscribe;

import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import lombok.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractRedisListener implements RedisListener, MessageListener {
    @NonNull
    private final Collection<String> names;

    private AbstractRedisListener(Collection<String> names) {
        this.names = names;
    }

    AbstractRedisListener(@NonNull ChannelNameEnum... channels) {
        this(Arrays.stream(channels).map(ChannelNameEnum::channel).collect(Collectors.toList()));
    }

    @Override
    public Collection<Topic> topics() {
        return names.stream().map(ChannelTopic::new).collect(Collectors.toList());
    }

    @Override
    public MessageListener listener() {
        return this;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        System.out.println("receive message: {}, on getGatewayChannel: {}" + message + channel);

        ChannelNameEnum channelNameEnum = ChannelNameEnum.from(channel);
        if (channelNameEnum != null) {
            handleMessage(channelNameEnum, message.getBody());
        }
    }

    abstract void handleMessage(ChannelNameEnum channelNameEnum, byte[] content);
}
