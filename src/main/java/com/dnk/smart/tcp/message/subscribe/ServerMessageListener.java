package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import com.dnk.smart.redis.data.pub.AppCommandRequestData;
import com.dnk.smart.redis.data.pub.AppCommandResponseData;
import com.dnk.smart.redis.data.pub.GatewayLoginData;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import com.dnk.smart.tcp.task.CommandProcessor;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.redis.data.dict.ChannelNameEnum.*;

@Component
public class ServerMessageListener extends AbstractRedisListener {
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private ClientMessageProcessor clientMessageProcessor;
    @Resource
    private CommandProcessor commandProcessor;
    ServerMessageListener() {
        super(GATEWAY_LOGIN, APP_COMMAND_REQUEST, APP_COMMAND_RESPONSE);
    }

    @Override
    void handleMessage(ChannelNameEnum channelNameEnum, byte[] content) {
        switch (channelNameEnum) {
            case GATEWAY_LOGIN:
                GatewayLoginData loginData = JSON.parseObject(content, GATEWAY_LOGIN.getClazz());
                if (!TCP_SERVER_ID.equals(loginData.getServerId())) {
                    sessionRegistry.closeGatewayChannelQuietly(loginData.getSn());
                }
                break;
            case APP_COMMAND_REQUEST:
                AppCommandRequestData requestData = JSON.parseObject(content, APP_COMMAND_REQUEST.getClazz());
                String sn = requestData.getSn();

                Channel channel = sessionRegistry.getGatewayChannel(sn);
                if (channel == null) {
                    sessionRegistry.awakeGatewayLogin(sn);
                }
                commandProcessor.startup(sn);
                break;
            case APP_COMMAND_RESPONSE:
                AppCommandResponseData responseData = JSON.parseObject(content, APP_COMMAND_RESPONSE.getClazz());
                String appId = responseData.getAppId();
                String result = responseData.getResult();

                clientMessageProcessor.responseAppCommandResult(appId, result);
                break;
            default:
                break;
        }
    }
}
