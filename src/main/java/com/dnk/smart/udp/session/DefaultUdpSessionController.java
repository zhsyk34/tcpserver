package com.dnk.smart.udp.session;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.config.Config;
import com.dnk.smart.dict.Action;
import com.dnk.smart.dict.Key;
import com.dnk.smart.dict.Result;
import com.dnk.smart.dict.redis.cache.UdpSessionData;
import com.dnk.smart.dict.udp.UdpInfo;
import com.dnk.smart.tcp.cache.CacheAccessor;
import com.dnk.smart.udp.UdpServer;
import com.dnk.smart.util.TimeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public final class DefaultUdpSessionController implements UdpSessionController {
    private static final Map<String, UdpInfo> GATEWAY_UDP_INFO_MAP = new ConcurrentHashMap<>();

    @Resource
    private CacheAccessor cacheAccessor;

    @Override
    public Channel channel() {
        return UdpServer.getChannel();
    }

    @Override
    public void receive(@NonNull UdpInfo info) {
        GATEWAY_UDP_INFO_MAP.put(info.getSn(), info);
        cacheAccessor.reportUdpSessionInfo(UdpSessionData.from(info));
    }

    @Override
    public void response(@NonNull InetSocketAddress target) {
        JSONObject json = new JSONObject();
        json.put(Key.RESULT.getName(), Result.OK.getName());
        send(target, json);
    }

    @Override
    public UdpInfo info(@NonNull String sn) {
        UdpInfo udpInfo = Optional.ofNullable(GATEWAY_UDP_INFO_MAP.get(sn)).orElse(UdpInfo.from(cacheAccessor.getUdpSessionInfo(sn)));
        return Optional.of(udpInfo).filter(info -> !TimeUtils.timeout(info.getHappen(), Config.UDP_INFO_EXPIRE)).orElse(null);
    }

    @Override
    public void awake(@NonNull InetSocketAddress target) {
        JSONObject json = new JSONObject();
        json.put(Key.ACTION.getName(), Action.LOGIN_INFORM.getName());
        send(target, json);
    }

    @Override
    public boolean awake(@NonNull String sn) {
        UdpInfo info = this.info(sn);
        System.out.println(info);
        if (info == null) {
            return false;
        }
        this.awake(new InetSocketAddress(info.getIp(), info.getPort()));
        return true;
    }

    private void send(InetSocketAddress target, JSONObject json) {
        Channel channel = this.channel();

        if (channel != null) {
            ByteBuf buf = Unpooled.copiedBuffer(json.toString().getBytes(CharsetUtil.UTF_8));
            channel.writeAndFlush(new DatagramPacket(buf, target));
        }
    }
}
