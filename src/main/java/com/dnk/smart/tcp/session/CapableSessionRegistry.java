package com.dnk.smart.tcp.session;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface CapableSessionRegistry {

    Channel getAcceptChannel(@NonNull String sn);

    Channel getGatewayChannel(@NonNull String sn);

    Channel getAppChannel(@NonNull String appId);

    boolean awakeGatewayLogin(@NonNull String sn);

    boolean closeGatewayChannelQuietly(@NonNull String sn);

}
