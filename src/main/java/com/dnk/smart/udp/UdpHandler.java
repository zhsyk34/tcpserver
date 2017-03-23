package com.dnk.smart.udp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.dict.Action;
import com.dnk.smart.dict.Key;
import com.dnk.smart.dict.Result;
import com.dnk.smart.dict.udp.UdpInfo;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.udp.session.UdpSessionController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
final class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Resource
    private UdpSessionController udpSessionController;

    private UdpInfo validate(DatagramPacket msg) {
        JSONObject json = JSON.parseObject(msg.content().toString(CharsetUtil.UTF_8));

        //1.
        Result result = Result.from(json.getString(Key.RESULT.getName()));
        if (result == Result.OK) {
            return null;
        }

        //2.
        Action action = Action.from(json.getString(Key.ACTION.getName()));
        String sn = json.getString(Key.SN.getName());
        String version = json.getString(Key.VERSION.getName());
        if (action == Action.HEART_BEAT && StringUtils.hasText(sn) && StringUtils.hasText(version)) {
            return UdpInfo.from(sn, msg.sender(), version);
        }
        return null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        Log.logger(Factory.UDP_RECEIVE, msg.content().toString());
        UdpInfo info = validate(msg);
        if (info != null) {
            udpSessionController.receive(info);
            udpSessionController.response(msg.sender());
        }
    }
}
