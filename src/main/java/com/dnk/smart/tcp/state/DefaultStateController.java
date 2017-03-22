package com.dnk.smart.tcp.state;

import com.dnk.smart.tcp.cache.CacheAccessor;
import com.dnk.smart.tcp.cache.dict.LoginInfo;
import com.dnk.smart.tcp.cache.dict.TcpInfo;
import com.dnk.smart.tcp.cache.dict.Verifier;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.tcp.cache.dict.Device.GATEWAY;

@Controller
public class DefaultStateController extends AbstractStateController implements StateController {
    @Resource
    private CacheAccessor cacheAccessor;
    /**
     * 初始连接注册
     * 登录成功注册
     * 连接关闭后注销
     */
    @Resource
    private SessionRegistry sessionRegistry;

    /**
     * 拒绝登录请求
     * 发送登录验证码
     * 拒绝错误的验证答复
     * 登录成功回复
     */
    @Resource
    private ClientMessageProcessor clientMessageProcessor;

    /**
     * 网关通过验证后请求端口分配
     * 网关登录成功后广播
     */
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;

    @Override
    public void onAccept(@NonNull Channel channel) {
        sessionRegistry.registerOnActive(channel);
    }

    @Override
    public void onRequest(@NonNull Channel channel) {
        if (cacheAccessor.info(channel).getDevice() == null) {
            clientMessageProcessor.refuseForLogin(channel);
        } else {
            //generator verifier
            Verifier verifier = Verifier.generator();
            cacheAccessor.verifier(channel, verifier);

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
        String ip = cacheAccessor.ip(channel);
        String sn = cacheAccessor.info(channel).getSn();
        int apply = cacheAccessor.info(channel).getApply();

        channelMessageProcessor.publishForAllocateUdpPort(ip, sn, apply);
    }

    @Override
    public void onSuccess(@NonNull Channel channel) {
        clientMessageProcessor.responseAfterLogin(channel);

        sessionRegistry.registerAfterLogin(channel);

        LoginInfo info = cacheAccessor.info(channel);
        if (info.getDevice() == GATEWAY) {
            cacheAccessor.registerGatewayTcpSessionInfo(TcpInfo.from(info));
            channelMessageProcessor.publishGatewayLogin(info.getSn(), TCP_SERVER_ID);
        }
    }

    @Override
    public void onClose(@NonNull Channel channel) {
        sessionRegistry.unRegisterAfterClose(channel);

        LoginInfo info = cacheAccessor.info(channel);
        if (info.getDevice() == GATEWAY) {
            cacheAccessor.unregisterGatewayTcpSessionInfo(info.getSn());
        }
    }
}
