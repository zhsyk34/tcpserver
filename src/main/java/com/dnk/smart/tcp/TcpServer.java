package com.dnk.smart.tcp;

import com.dnk.smart.config.Config;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public final class TcpServer {
    @Getter
    private volatile boolean started = false;

    @Resource
    private ChannelInitializer ChannelInitializer;

    public void startup() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup handleGroup = new NioEventLoopGroup();

        bootstrap.group(mainGroup, handleGroup).channel(NioServerSocketChannel.class);

        //setting options
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, Config.TCP_SERVER_BACKLOG);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Config.TCP_CONNECT_TIMEOUT * 1000);

        //pool
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        //logging
        bootstrap.childHandler(new LoggingHandler(LogLevel.WARN));

        //handler
        bootstrap.childHandler(ChannelInitializer);

        try {
            Channel channel = bootstrap.bind(Config.TCP_SERVER_HOST, Config.TCP_SERVER_PORT).sync().channel();

            Log.logger(Factory.TCP_EVENT, TcpServer.class.getSimpleName() + " startup success at port[" + Config.TCP_SERVER_PORT + "]");
            started = true;

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mainGroup.shutdownGracefully();
            handleGroup.shutdownGracefully();
        }
    }

}
