package com.dnk.smart.tcp.session;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface CapableSessionRegistry {

    Channel getAcceptChannel(@NonNull String sn);

    Channel getGatewayChannel(@NonNull String sn);

    Channel getAppChannel(@NonNull String appId);

    boolean awakeGatewayLogin(@NonNull String sn);

    /**
     * 收到网关在其它服务器登录的广播时,静默关闭本服务器上可能存在的过期连接
     */
    boolean closeGatewayChannelQuietly(@NonNull String sn);

}
