package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.tcp.command.CommandProcessor;
import com.dnk.smart.tcp.message.data.AppCommandRequestData;
import com.dnk.smart.tcp.message.data.AppCommandResponseData;
import com.dnk.smart.tcp.message.data.GatewayLoginData;
import com.dnk.smart.tcp.message.dict.RedisChannel;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.tcp.message.dict.RedisChannel.*;

@Service
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
    void handleMessage(RedisChannel channelNameEnum, byte[] content) {
        Channel channel;
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

                channel = sessionRegistry.getGatewayChannel(sn);
                if (channel == null) {
                    sessionRegistry.awakeGatewayLogin(sn);
                }
                commandProcessor.startup(sn);
                break;
            case APP_COMMAND_RESPONSE:
                AppCommandResponseData responseData = JSON.parseObject(content, APP_COMMAND_RESPONSE.getClazz());
                String appId = responseData.getAppId();

                channel = sessionRegistry.getAppChannel(appId);
                Optional.ofNullable(channel).ifPresent(c -> clientMessageProcessor.responseAppCommandResult(c, responseData.getResult()));
                break;
            default:
                break;
        }
    }
}
