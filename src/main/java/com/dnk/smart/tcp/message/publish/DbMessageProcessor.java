package com.dnk.smart.tcp.message.publish;

import lombok.NonNull;

/**
 * 与dbServer通讯
 */
public interface DbMessageProcessor {

    /**
     * 请求端口分配
     */
    void publishForAllocateUdpPort(@NonNull String ip, @NonNull String sn, int apply);

    /**
     * 请求版本信息
     */
    void publishForGatewayVersion(@NonNull String sn);

    /**
     * 推送信息上报
     */
    void publishPushMessage(@NonNull String sn, @NonNull String message);
}
