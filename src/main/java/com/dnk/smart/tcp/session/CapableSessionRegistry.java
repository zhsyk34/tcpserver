package com.dnk.smart.tcp.session;

import com.dnk.smart.tcp.task.LoopTask;
import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.List;

public interface CapableSessionRegistry {

    Channel getAcceptChannel(@NonNull String sn);

    Channel getGatewayChannel(@NonNull String sn);

    Channel getAppChannel(@NonNull String appId);

    /**
     * 收到网关在其它服务器登录的广播时,静默关闭本服务器上可能存在的过期连接(不进行其它的回调处理)
     */
    boolean closeGatewayChannelQuietly(@NonNull String sn);

    /**
     * 监视连接
     */
    List<LoopTask> monitor();

}
