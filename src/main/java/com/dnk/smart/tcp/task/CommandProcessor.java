package com.dnk.smart.tcp.task;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface CommandProcessor {

    void prepare(@NonNull String sn);

    void prepare(@NonNull Channel channel);

    void execute(@NonNull Channel channel);

    void startup(@NonNull Channel channel);

    void rest(@NonNull Channel channel);
}
