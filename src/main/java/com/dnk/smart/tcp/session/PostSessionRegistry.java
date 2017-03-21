package com.dnk.smart.tcp.session;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface PostSessionRegistry {

    void registerOnActive(@NonNull Channel channel);

    void registerAfterLogin(@NonNull Channel channel);

    boolean unRegisterAfterClose(@NonNull Channel channel);

}
