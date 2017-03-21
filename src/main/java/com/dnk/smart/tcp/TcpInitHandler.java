package com.dnk.smart.tcp;

import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
final class TcpInitHandler extends ChannelInboundHandlerAdapter {
    @Resource
    private SessionRegistry sessionRegistry;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channel.close();
        sessionRegistry.registerOnActive(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionRegistry.unRegisterAfterClose(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.channelInactive(ctx);
    }

    @Override
    public boolean isSharable() {
        return true;
    }
}
