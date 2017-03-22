package com.dnk.smart.tcp.message.publish;

import lombok.NonNull;

/**
 * 与其它tcpServer通讯
 */
public interface ServerMessageProcessor {

    /**
     * 网关登录成功后发布通告,以释放其它服务器可能存在的过时连接
     */
    void publishGatewayLogin(@NonNull String sn, @NonNull String serverId);

    /**
     * 网关接收到app请求时发布通知
     * app连接的服务器与网关登录服务器可能不同
     * 请求结果做同样处理
     *
     * @see #publishAppCommandResult(String, String)
     */
    void publishAppCommandRequest(@NonNull String sn);

    /**
     * app请求指令处理结果发布
     *
     * @see #publishAppCommandRequest(String)
     */
    void publishAppCommandResult(@NonNull String appId, @NonNull String result);

}