package com.dnk.smart.tcp.cache;

import com.dnk.smart.tcp.cache.dict.Command;
import com.dnk.smart.tcp.cache.dict.TcpInfo;
import com.dnk.smart.tcp.cache.dict.UdpInfo;
import lombok.NonNull;

import java.util.List;

/**
 * redis-Server数据读写
 */
public interface RedisCacheAccessor {
    /**
     * 网关登录后登记
     *
     * @param info 网关tcp连接信息
     */
    void registerGatewayTcpSessionInfo(@NonNull TcpInfo info);

    /**
     * 获取网关tcp连接信息,判断是否在线
     */
    TcpInfo getGatewayTcpSessionInfo(@NonNull String sn);

    /**
     * 网关下线后注销登记
     *
     * @param sn 网关序列号
     */
    void unregisterGatewayTcpSessionInfo(@NonNull String sn);

    /**
     * (定时)上报更新网关心跳相关信息
     *
     * @param info 网关udp心跳数据
     */
    void reportUdpSessionInfo(@NonNull UdpInfo info);

    /**
     * (定时)上报服务器状态
     *
     * @param serverId tcpServer的编号
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
     * 获取待处理的第一条请求指令
     *
     * @param sn 网关序列号
     */
    Command getFirstCommand(@NonNull String sn);

    /**
     * 获取待处理的所有请求指令
     *
     * @param sn 网关序列号
     */
    List<Command> getAllCommand(@NonNull String sn);

}
