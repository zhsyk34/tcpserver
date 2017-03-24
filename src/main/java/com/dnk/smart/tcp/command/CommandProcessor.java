package com.dnk.smart.tcp.command;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface CommandProcessor {

    void prepare(@NonNull Channel channel);

    void execute(@NonNull Channel channel);

    void startup(@NonNull String sn);

    void startup(@NonNull Channel channel);

    void reset(@NonNull Channel channel);

    void restart(@NonNull Channel channel);

    /**
     * 网关唤醒失败时执行
     */
    void clean(@NonNull String sn);

    /**
     * 网关下线时执行
     */
    void clean(@NonNull Channel channel);
}
