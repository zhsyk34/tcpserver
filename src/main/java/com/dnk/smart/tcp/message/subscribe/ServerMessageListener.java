package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.dict.redis.RedisChannel;
import com.dnk.smart.dict.redis.channel.AppCommandRequestData;
import com.dnk.smart.dict.redis.channel.AppCommandResponseData;
import com.dnk.smart.dict.redis.channel.GatewayAwakeFailData;
import com.dnk.smart.dict.redis.channel.GatewayLoginData;
import com.dnk.smart.tcp.awake.AwakeService;
import com.dnk.smart.tcp.command.CommandProcessor;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.dict.redis.RedisChannel.*;

@Service
public class ServerMessageListener extends AbstractRedisListener {
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private AwakeService awakeService;
    @Resource
    private ClientMessageProcessor clientMessageProcessor;
    @Resource
    private CommandProcessor commandProcessor;

    ServerMessageListener() {
        super(GATEWAY_AWAKE_FAIL, GATEWAY_LOGIN, APP_COMMAND_REQUEST, APP_COMMAND_RESPONSE);
    }

    @Override
    void handleMessage(RedisChannel channelNameEnum, byte[] content) {
        Channel channel;
        String sn;

        switch (channelNameEnum) {
            case GATEWAY_AWAKE_FAIL:
                GatewayAwakeFailData failData = JSON.parseObject(content, GATEWAY_LOGIN.getClazz());
                sn = failData.getSn();

                if (TCP_SERVER_ID.equals(failData.getServerId())) {
                    commandProcessor.clean(sn);
                } else {
                    awakeService.cancel(sn);
                }
                break;
            case GATEWAY_LOGIN:
                GatewayLoginData loginData = JSON.parseObject(content, GATEWAY_LOGIN.getClazz());
                if (!TCP_SERVER_ID.equals(loginData.getServerId())) {
                    sn = loginData.getSn();
                    sessionRegistry.closeGatewayChannelQuietly(sn);
                    commandProcessor.startup(sn);
                }
                break;
            case APP_COMMAND_REQUEST:
                AppCommandRequestData requestData = JSON.parseObject(content, APP_COMMAND_REQUEST.getClazz());
                sn = requestData.getSn();

                channel = sessionRegistry.getGatewayChannel(sn);
                if (channel == null) {
                    awakeService.execute(sn);
                }
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
