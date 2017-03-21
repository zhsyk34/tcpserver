package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import com.dnk.smart.redis.data.pub.GatewayUdpPortAllocateData;
import com.dnk.smart.redis.data.pub.GatewayVersionResponseData;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import com.dnk.smart.tcp.state.StateController;
import io.netty.channel.Channel;

import javax.annotation.Resource;

import static com.dnk.smart.redis.data.dict.ChannelNameEnum.GATEWAY_UDP_PORT_ALLOCATE;
import static com.dnk.smart.redis.data.dict.ChannelNameEnum.GATEWAY_VERSION_RESPONSE;

public class DbMessageListener extends AbstractRedisListener {

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
    void handleMessage(ChannelNameEnum channelNameEnum, byte[] content) {
        String sn;
        Channel channel;

        switch (channelNameEnum) {
            case GATEWAY_VERSION_RESPONSE:
                GatewayVersionResponseData versionData = JSON.parseObject(content, GATEWAY_VERSION_RESPONSE.getClazz());
                sn = versionData.getSn();
                String result = versionData.getResult();

                channel = sessionRegistry.getGatewayChannel(sn);
                if (channel != null) {
                    clientMessageProcessor.responseVerion(channel, result);
                }
                break;
            case GATEWAY_UDP_PORT_ALLOCATE:
                GatewayUdpPortAllocateData portData = JSON.parseObject(content, GATEWAY_UDP_PORT_ALLOCATE.getClazz());
                sn = portData.getSn();
                int allocated = portData.getAllocated();

                channel = sessionRegistry.getAcceptChannel(sn);
                if (channel != null) {
                    stateController.success(channel, allocated);
                }
                break;
            default:
                break;
        }
    }
}
