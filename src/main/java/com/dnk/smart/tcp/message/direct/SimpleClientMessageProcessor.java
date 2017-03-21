package com.dnk.smart.tcp.message.direct;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.dict.Key;
import com.dnk.smart.dict.Result;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SimpleClientMessageProcessor implements ClientMessageProcessor {
    @Resource
    private SessionRegistry registry;

    @Override
    public void refuseForLogin(@NonNull Channel channel) {

    }

    @Override
    public void sendVerificationQuestion(@NonNull Channel channel, @NonNull String question) {

    }

    @Override
    public void refuseForVerificationAnswer(@NonNull Channel channel) {

    }

    @Override
    public void responseAfterLogin(@NonNull Channel channel) {

    }

    @Override
    public void responseHeartbeat(@NonNull Channel channel) {
        JSONObject json = new JSONObject();
        json.put(Key.RESULT.getName(), Result.OK.getName());
        channel.writeAndFlush(json);
    }

    @Override
    public void responseAppCommandResult(@NonNull String appId, @NonNull String result) {
        Channel channel = registry.getAppChannel(appId);
        if (channel != null) {
            channel.writeAndFlush(result);
        }
    }
}
