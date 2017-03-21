package com.dnk.smart.tcp.message.subscribe;

import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import lombok.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRedisListener implements RedisListener, MessageListener {
    @NonNull
    private final Collection<String> names;

    private AbstractRedisListener(Collection<String> names) {
        this.names = names;
    }

    AbstractRedisListener(@NonNull ChannelNameEnum first, ChannelNameEnum... channelNameEnums) {
        //TODO
        List<String> r = Arrays.stream(channelNameEnums).map(ChannelNameEnum::channel).collect(
                () -> {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(first.channel());
                    return list;
                },
                ArrayList::add,
                ArrayList::addAll
        );
        this.names = r;
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
//        Log.logger("receive message: {}, on channel: {}", message, channel);

        ChannelNameEnum channelNameEnum = ChannelNameEnum.from(channel);
        if (channelNameEnum != null) {
            handleMessage(channelNameEnum, message.getBody());
        }
    }

    abstract void handleMessage(ChannelNameEnum channelNameEnum, byte[] content);
}
