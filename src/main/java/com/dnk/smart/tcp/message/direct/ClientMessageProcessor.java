package com.dnk.smart.tcp.message.direct;

import io.netty.channel.Channel;
import lombok.NonNull;

/**
 * 与终端 app/gateway 直接通讯
 */
public interface ClientMessageProcessor {

    /**
     * 拒绝错误的登录请求
     */
    void refuseForLogin(@NonNull Channel channel);

    /**
     * 发送登录验证码
     */
    void sendVerificationQuestion(@NonNull Channel channel, @NonNull String question);

    /**
     * 验证错误反馈
     */
    void refuseForVerificationAnswer(@NonNull Channel channel);

    /**
     * 登录成功后回复
     */
    void responseAfterLogin(@NonNull Channel channel);

    /**
     * 网关(登录后)的心跳请求回复
     */
    void responseHeartbeat(@NonNull Channel channel);

    /**
     * 网关(登录后)的版本信息请求回复
     */
    void responseVersion(@NonNull Channel channel, @NonNull String result);

    /**
     * app请求指令处理结果回复
     */
    void responseAppCommandResult(@NonNull String appId, @NonNull String result);

}
