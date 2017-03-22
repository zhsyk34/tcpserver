package com.dnk.smart.tcp.message.publish;

import com.dnk.smart.tcp.message.data.*;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import static com.dnk.smart.tcp.message.dict.RedisChannel.*;

@Service
public class DefaultChannelMessageProcessor extends SimpleRedisPublisher implements ChannelMessageProcessor {

    @Override
    public void publishWebCommandResult(String webServerId, boolean result) {
        super.publish(WEB_COMMAND_RESPONSE, WebCommandResponseData.of(webServerId, result));
    }

    @Override
    public void publishForAllocateUdpPort(@NonNull String ip, @NonNull String sn, int apply) {
        super.publish(GATEWAY_UDP_PORT_APPLY, GatewayUdpPortApplyData.of(ip, sn, apply));
    }

    @Override
    public void publishForGatewayVersion(@NonNull String sn) {
        super.publish(GATEWAY_VERSION_REQUEST, GatewayVersionRequestData.of(sn));
    }

    @Override
    public void publishPushMessage(@NonNull String sn, @NonNull String message) {
        super.publish(GATEWAY_MESSAGE_PUSH, GatewayMessagePushData.of(sn, message));
    }

    @Override
    public void publishGatewayLogin(@NonNull String sn, @NonNull String serverId) {
        super.publish(GATEWAY_LOGIN, GatewayLoginData.of(sn, serverId));
    }

    @Override
    public void publishAppCommandRequest(@NonNull String sn) {
        super.publish(APP_COMMAND_REQUEST, AppCommandRequestData.of(sn));
    }

    @Override
    public void publishAppCommandResult(@NonNull String appId, @NonNull String result) {
        super.publish(WEB_COMMAND_RESPONSE, AppCommandResponseData.of(appId, result));
    }

}
