package com.dnk.smart.redis.data.dict;

import com.dnk.smart.redis.data.pub.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * redis pub/sub channel name
 */
@RequiredArgsConstructor
@Getter
public enum ChannelNameEnum {
    /**
     * tcpServer---tcpServer
     */
    GATEWAY_LOGIN("网关登录", GatewayLoginData.class),
    APP_COMMAND_REQUEST("转发app请求", AppCommandRequestData.class),
    APP_COMMAND_RESPONSE("app指令处理结果", AppCommandResponseData.class),

    /**
     * tcpServer---webServer
     */
    WEB_COMMAND_REQUEST("控制指令请求", WebCommandRequestData.class),
    WEB_COMMAND_RESPONSE("控制指令响应", WebCommandResponseData.class),

    /**
     * tcpServer---dbServer
     */
    GATEWAY_VERSION_REQUEST("网关版本查询请求", GatewayVersionRequestData.class),
    GATEWAY_VERSION_RESPONSE("网关版本查询响应", GatewayVersionResponseData.class),
    GATEWAY_MESSAGE_PUSH("网关主动推送(报警)信息", GatewayMessagePushData.class),
    GATEWAY_UDP_PORT_APPLY("网关udp端口申请", GatewayUdpPortApplyData.class),
    GATEWAY_UDP_PORT_ALLOCATE("网关udp端口分配", GatewayUdpPortAllocateData.class);

    private static final Map<String, ChannelNameEnum> CHANNEL_MAP = new HashMap<>();

    static {
        for (ChannelNameEnum channelNameEnum : values()) {
            CHANNEL_MAP.put(channelNameEnum.channel(), channelNameEnum);
        }
    }

    private final String description;
    private final Class<?> clazz;

    public static ChannelNameEnum from(String channel) {
        return CHANNEL_MAP.get(channel);
    }

    public static void main(String[] args) {
        System.out.println(GATEWAY_VERSION_REQUEST.channel());
    }

    public String channel() {
        return this.name().replace("_", ":").toLowerCase();
    }

}
