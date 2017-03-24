package com.dnk.smart.udp;

import com.dnk.smart.config.Config;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * record udp session to awake gateway
 */
@Service
public final class UdpServer {
    @Getter
    private static volatile Channel channel;

    @Resource
    private UdpCoder udpCoder;
    @Resource
    private UdpHandler udpHandler;

    public void startup() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group).channel(NioDatagramChannel.class);
            bootstrap.option(ChannelOption.SO_BROADCAST, false);
            bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel ch) throws Exception {
                    ch.pipeline().addLast(udpCoder, udpHandler);
                }
            });

            channel = bootstrap.bind(Config.UDP_SERVER_PORT).syncUninterruptibly().channel();

            Log.logger(Factory.UDP_EVENT, UdpServer.class.getSimpleName() + " startup success at port [" + Config.UDP_SERVER_PORT + "]");

            channel.closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
