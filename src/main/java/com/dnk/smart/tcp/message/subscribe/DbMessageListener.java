package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.config.Config;
import com.dnk.smart.tcp.message.data.GatewayUdpPortAllocateData;
import com.dnk.smart.tcp.message.data.GatewayVersionResponseData;
import com.dnk.smart.tcp.message.dict.RedisChannel;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import com.dnk.smart.tcp.state.StateController;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.dnk.smart.tcp.message.dict.RedisChannel.GATEWAY_UDP_PORT_ALLOCATE;
import static com.dnk.smart.tcp.message.dict.RedisChannel.GATEWAY_VERSION_RESPONSE;

@Service
public final class DbMessageListener extends AbstractRedisListener {

    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private ClientMessageProcessor clientMessageProcessor;
    @Resource
    private StateController stateController;

    DbMessageListener() {
        super(GATEWAY_VERSION_RESPONSE, GATEWAY_UDP_PORT_ALLOCATE);
    }

    @Override
    void handleMessage(RedisChannel redisChannel, byte[] content) {
        Channel channel;

        switch (redisChannel) {
            case GATEWAY_VERSION_RESPONSE:
                GatewayVersionResponseData versionData = JSON.parseObject(content, GATEWAY_VERSION_RESPONSE.getClazz());

                channel = sessionRegistry.getGatewayChannel(versionData.getSn());
                if (channel != null) {
                    clientMessageProcessor.responseVersion(channel, versionData.getResult());
                }
                break;
            case GATEWAY_UDP_PORT_ALLOCATE:
                GatewayUdpPortAllocateData portData = JSON.parseObject(content, GATEWAY_UDP_PORT_ALLOCATE.getClazz());

                channel = sessionRegistry.getAcceptChannel(portData.getSn());
                if (channel == null) {
                    return;
                }

                int allocated = portData.getAllocated();
                if (allocated < Config.TCP_ALLOT_MIN_UDP_PORT) {
                    stateController.close(channel);
                }

                stateController.success(channel, allocated);
                break;
            default:
                break;
        }
    }
}
