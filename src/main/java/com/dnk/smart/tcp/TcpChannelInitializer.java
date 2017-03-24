package com.dnk.smart.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ChannelHandler.Sharable
final class TcpChannelInitializer extends ChannelInitializer {
    @Resource
    private TcpInitHandler initHandler;
    //    @Resource
//    private TcpDecoder decoder;
//    @Resource
//    private TcpEncoder encoder;
    @Resource
    private TcpLoginHandler loginHandler;
    @Resource
    private TcpMessageHandler messageHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(initHandler, new TcpDecoder(), new TcpEncoder(), loginHandler, messageHandler);
//        ch.pipeline().addLast(initHandler, decoder, encoder, loginHandler, messageHandler);
    }
}