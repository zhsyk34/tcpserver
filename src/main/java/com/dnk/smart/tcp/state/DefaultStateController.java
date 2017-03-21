package com.dnk.smart.tcp.state;

import com.dnk.smart.tcp.cache.DataAccessor;
import com.dnk.smart.tcp.cache.LoginInfo;
import com.dnk.smart.tcp.cache.TcpInfo;
import com.dnk.smart.tcp.cache.Verifier;
import com.dnk.smart.tcp.message.cache.RedisMessageAccessor;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.tcp.cache.Device.GATEWAY;

@Controller
public class DefaultStateController extends AbstractStateController implements StateController {
    @Resource
    private DataAccessor dataAccessor;
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private RedisMessageAccessor redisMessageAccessor;
    @Resource
    private ClientMessageProcessor clientMessageProcessor;
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;

    @Override
    public void onAccept(@NonNull Channel channel) {
        sessionRegistry.registerOnActive(channel);
    }

    @Override
    public void onRequest(@NonNull Channel channel) {
        if (dataAccessor.info(channel).getDevice() == null) {
            clientMessageProcessor.refuseForLogin(channel);
        } else {
            //generator verifier
            Verifier verifier = Verifier.generator();
            dataAccessor.verifier(channel, verifier);

            clientMessageProcessor.sendVerificationQuestion(channel, verifier.getQuestion());
        }
    }

    @Override
    public void onVerify(@NonNull Channel channel, boolean result) {
        if (result) {
            super.await(channel);
        } else {
            clientMessageProcessor.refuseForVerificationAnswer(channel);
            super.close(channel);
        }
    }

    @Override
    public void onAwait(@NonNull Channel channel) {
        String ip = dataAccessor.ip(channel);
        String sn = dataAccessor.info(channel).getSn();
        int apply = dataAccessor.info(channel).getApply();

        channelMessageProcessor.publishForAllocateUdpPort(ip, sn, apply);
    }

    @Override
    public void onSuccess(@NonNull Channel channel) {
        clientMessageProcessor.responseAfterLogin(channel);
        sessionRegistry.registerAfterLogin(channel);

        LoginInfo info = dataAccessor.info(channel);
        if (info.getDevice() == GATEWAY) {
            redisMessageAccessor.registerGatewayTcpSessionInfo(TcpInfo.from(info));
            channelMessageProcessor.publishGatewayLogin(info.getSn(), TCP_SERVER_ID);
        }
    }

    @Override
    public void onClose(@NonNull Channel channel) {
        sessionRegistry.unRegisterAfterClose(channel);

        LoginInfo info = dataAccessor.info(channel);
        if (info.getDevice() == GATEWAY) {
            redisMessageAccessor.unregisterGatewayTcpSessionInfo(info.getSn());
        }
    }
}
