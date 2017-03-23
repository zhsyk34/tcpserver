package com.dnk.smart.tcp.command;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface CommandProcessor {

    void prepare(@NonNull String sn);

    void prepare(@NonNull Channel channel);

    void execute(@NonNull Channel channel);

    void startup(@NonNull String sn);

    void startup(@NonNull Channel channel);

    void rest(@NonNull Channel channel);

    void clean(@NonNull String sn);

    void clean(@NonNull Channel channel);
}
