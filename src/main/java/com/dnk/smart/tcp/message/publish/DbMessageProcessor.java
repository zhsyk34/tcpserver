package com.dnk.smart.tcp.message.publish;

import lombok.NonNull;

/**
 * 与dbServer通讯
 */
public interface DbMessageProcessor {

    /**
     * 网关登录成功后请求端口分配
     */
    void publishForAllocateUdpPort(@NonNull String ip, @NonNull String sn, int apply);

    /**
     * 网关请求最新版本信息
     */
    void publishForGatewayVersion(@NonNull String sn);

    /**
     * 网关主动上报的报警等推送信息
     */
    void publishPushMessage(@NonNull String sn, @NonNull String message);
}
