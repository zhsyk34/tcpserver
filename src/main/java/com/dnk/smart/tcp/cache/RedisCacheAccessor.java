package com.dnk.smart.tcp.cache;

import com.dnk.smart.tcp.cache.dict.Command;
import com.dnk.smart.tcp.cache.dict.TcpInfo;
import com.dnk.smart.tcp.cache.dict.UdpInfo;
import lombok.NonNull;

import java.util.List;

public interface RedisCacheAccessor {
    /**
     * 网关登录后登记
     */
    void registerGatewayTcpSessionInfo(@NonNull TcpInfo info);

    /**
     * 网关下线后通知
     */
    void unregisterGatewayTcpSessionInfo(@NonNull String sn);

    /**
     * 更新网关心跳相关信息
     */
    void reportUdpSessionInfo(@NonNull UdpInfo info);

    /**
     * 上报服务器状态
     */
    void reportServerStatus(@NonNull String serverId);

    /**
     * 提交(共享)app的请求指令
     *
     * @param appId   app连接的channelId
     * @param command 指令
     */
    void shareAppCommand(@NonNull String appId, @NonNull String command);

    /**
     * 获取第一条指令
     */
    Command getFirstCommand(@NonNull String sn);

    /**
     * 获取所有指令
     */
    List<Command> getAllCommand(@NonNull String sn);

}
