package com.dnk.smart.tcp.message.publish;

import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import com.dnk.smart.redis.data.pub.*;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class DefaultChannelMessageProcessor extends DefaultRedisPublisher implements ChannelMessageProcessor {
    @Override
    public void publishWebCommandResult(String webServerId, boolean result) {
        super.publish(ChannelNameEnum.WEB_COMMAND_RESPONSE, WebCommandResponseData.of(webServerId, result));
    }

    @Override
    public void publishForAllocateUdpPort(@NonNull String ip, @NonNull String sn, int apply) {
        super.publish(ChannelNameEnum.GATEWAY_UDP_PORT_APPLY, GatewayUdpPortApplyData.of(ip, sn, apply));
    }

    @Override
    public void publishForGatewayVersion(@NonNull String sn) {
        super.publish(ChannelNameEnum.GATEWAY_VERSION_REQUEST, GatewayVersionRequestData.of(sn));
    }

    @Override
    public void publishPushMessage(@NonNull String sn, @NonNull String message) {
        super.publish(ChannelNameEnum.GATEWAY_MESSAGE_PUSH, GatewayMessagePushData.of(sn, message));
    }

    @Override
    public void publishGatewayLogin(@NonNull String sn, @NonNull String serverId) {
        super.publish(ChannelNameEnum.GATEWAY_LOGIN, GatewayLoginData.of(sn, serverId));
    }

    @Override
    public void publishAppCommandRequest(@NonNull String sn) {
        super.publish(ChannelNameEnum.APP_COMMAND_REQUEST, AppCommandRequestData.of(sn));
    }

    @Override
    public void publishAppCommandResult(@NonNull String appId, @NonNull String result) {
        super.publish(ChannelNameEnum.WEB_COMMAND_RESPONSE, AppCommandResponseData.of(appId, result));
    }
}
