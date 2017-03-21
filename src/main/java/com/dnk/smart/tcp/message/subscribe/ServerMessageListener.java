package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import com.dnk.smart.redis.data.pub.AppCommandRequestData;
import com.dnk.smart.redis.data.pub.AppCommandResponseData;
import com.dnk.smart.redis.data.pub.GatewayLoginData;
import com.dnk.smart.tcp.session.SessionRegistry;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.redis.data.dict.ChannelNameEnum.*;

@Component
public class ServerMessageListener extends AbstractRedisListener {

    @Resource
    private SessionRegistry sessionRegistry;

    ServerMessageListener() {
        super(GATEWAY_LOGIN, APP_COMMAND_REQUEST, APP_COMMAND_RESPONSE);
    }

    public static void main(String[] args) {
        new ServerMessageListener();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
//        Log.logger("receive message: {}, on channel: {}", message, channel);

        ChannelNameEnum channelNameEnum = ChannelNameEnum.from(channel);
        if (channelNameEnum == null) {
            return;
        }

        byte[] content = message.getBody();

        switch (channelNameEnum) {
            case GATEWAY_LOGIN:
                GatewayLoginData loginData = JSON.parseObject(content, GATEWAY_LOGIN.getClazz());
                if (TCP_SERVER_ID.equals(loginData.getServerId())) {
                    sessionRegistry.close(loginData.getSn());
                }
                break;
            case APP_COMMAND_REQUEST:
                AppCommandRequestData requestData = JSON.parseObject(content, APP_COMMAND_REQUEST.getClazz());
                String sn = requestData.getSn();
                //TODO
                break;
            case APP_COMMAND_RESPONSE:
                AppCommandResponseData responseData = JSON.parseObject(content, APP_COMMAND_RESPONSE.getClazz());
                String appId = responseData.getAppId();
                String result = responseData.getResult();
                //TODO
                break;
            default:
                break;
        }
    }
}
