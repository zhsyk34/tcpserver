package com.dnk.smart.tcp.message.publish;

import lombok.NonNull;

/**
 * 与其它tcpServer通讯
 */
public interface ServerMessageProcessor {

    /**
     * 网关登录成功后发布通告
     */
    void publishGatewayLogin(@NonNull String sn, @NonNull String serverId);

    /**
     * 发布app指令请求事件
     */
    void publishAppCommandRequest(@NonNull String sn);

    /**
     * 发布app指令请求处理结果
     */
    void publishAppCommandResult(@NonNull String appId, @NonNull String result);

}
