package com.dnk.smart.tcp.session;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface PostSessionRegistry {

    void registerOnActive(@NonNull Channel channel);

    /**
     * 网关登录前重新以sn进行注册以便监听到端口分配响应时快速响应
     */
    void registerAgainBeforeLogin(@NonNull Channel channel);

    void registerAfterLogin(@NonNull Channel channel);

    void unRegisterAfterClose(@NonNull Channel channel);

}
