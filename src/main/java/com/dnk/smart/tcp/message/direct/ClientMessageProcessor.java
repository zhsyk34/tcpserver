package com.dnk.smart.tcp.message.direct;

import io.netty.channel.Channel;
import lombok.NonNull;

/**
 * 与终端 app/gateway 通讯
 */
public interface ClientMessageProcessor {
    /**
     * 登录请求回复
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
     * (登录成功)后心跳回复
     */
    void responseHeartbeat(@NonNull Channel channel);

    /**
     * 版本信息请求回复
     */
    void responseVerion(@NonNull Channel channel, @NonNull String result);

    /**
     * 指令处理结果响应
     */
    void responseAppCommandResult(@NonNull String appId, @NonNull String result);

}
