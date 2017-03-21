package com.dnk.smart.tcp;

import com.dnk.smart.tcp.state.StateController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
final class TcpInitHandler extends ChannelInboundHandlerAdapter {
    @Resource
    private StateController stateController;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        stateController.accept(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        stateController.close(ctx.channel());
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
