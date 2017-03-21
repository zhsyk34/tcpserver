package com.dnk.smart.tcp.session;

import com.dnk.smart.config.Config;
import com.dnk.smart.kit.ThreadKit;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.tcp.cache.DataAccessor;
import com.dnk.smart.tcp.cache.dict.Device;
import com.dnk.smart.tcp.cache.dict.LoginInfo;
import com.dnk.smart.tcp.cache.dict.TcpInfo;
import com.dnk.smart.tcp.cache.dict.UdpInfo;
import com.dnk.smart.udp.session.UdpSessionController;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultSessionRegistry implements SessionRegistry {
    private static final Map<String, Channel> ACCEPT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Channel> GATEWAY_MAP = new ConcurrentHashMap<>(Config.TCP_GATEWAY_COUNT_PREDICT);
    private static final Map<String, Channel> APP_MAP = new ConcurrentHashMap<>(Config.TCP_APP_COUNT_PREDICT);

    @Resource
    private DataAccessor dataAccessor;
    @Resource
    private UdpSessionController udpSessionController;

    @Override
    public void registerOnActive(@NonNull Channel channel) {
        Log.logger(Factory.TCP_EVENT, "[" + dataAccessor.ip(channel) + ":" + dataAccessor.port(channel) + "] 连接成功");
        Channel original = ACCEPT_MAP.put(dataAccessor.id(channel), channel);
        if (original != null) {
            original.close();
        }
    }

    @Override
    public void registerAfterLogin(@NonNull Channel channel) {
        String id = dataAccessor.id(channel);

        if (ACCEPT_MAP.remove(id) == null) {
//            Log.logger(Factory.TCP_EVENT, "[" + dataAccessor.info(getGatewayChannel).getSn() + "]登录超时(失败)");
            return;
        }

        LoginInfo info = dataAccessor.info(channel);
        if (info == null) {
            return;
        }

        Channel original;
        switch (info.getDevice()) {
            case APP:
                original = APP_MAP.put(id, channel);
                Log.logger(Factory.TCP_EVENT, "app[" + id + "]登录成功");
                break;
            case GATEWAY:
                original = GATEWAY_MAP.put(info.getSn(), channel);

                dataAccessor.registerGatewayTcpSessionInfo(TcpInfo.from(dataAccessor.info(channel)));

                Log.logger(Factory.TCP_EVENT, "网关[" + info.getSn() + "]登录成功");
                break;
            default:
                return;
        }

        if (original != null) {
            Log.logger(Factory.TCP_EVENT, "关闭原有的连接[" + dataAccessor.info(original).getHappen() + "]");
            original.close();
        }
    }

    @Override
    public boolean unRegisterAfterClose(@NonNull Channel channel) {
        String id = dataAccessor.id(channel);
        if (ACCEPT_MAP.remove(id, channel)) {
            return true;
        }

        LoginInfo info = dataAccessor.info(channel);
        if (info == null) {
            //normally it won't happen
            return false;
        }

        Device device = info.getDevice();
        if (device == null) {
            //normally it won't happen
            return false;
        }

        //the getGatewayChannel has enter into login
        switch (device) {
            case APP:
                if (APP_MAP.remove(id, channel)) {
                    return true;
                }
                Log.logger(Factory.TCP_ERROR, channel.remoteAddress() + "客户端关闭出错,在APP队列中查找不到该连接(可能因为线时长已到被移除)");
                return false;
            case GATEWAY:
                if (GATEWAY_MAP.remove(info.getSn(), channel)) {
                    dataAccessor.unregisterGatewayTcpSessionInfo(dataAccessor.info(channel).getSn());

                    Log.logger(Factory.TCP_EVENT, "网关[" + info.getSn() + "]下线");
                    return true;
                }
                Log.logger(Factory.TCP_ERROR, channel.remoteAddress() + " 网关关闭出错,在网关队列中查找不到该连接(可能因在线时长已到被移除或重新登录时被关闭)");
                return false;
            default:
                Log.logger(Factory.TCP_ERROR, "关闭出错,非法的登录数据");
                return false;
        }
    }

    @Override
    public Channel getAcceptChannel(@NonNull String sn) {
        //TODO:
        for (Channel channel : ACCEPT_MAP.values()) {
            if (sn.equals(dataAccessor.info(channel).getSn())) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel getGatewayChannel(@NonNull String sn) {
        return GATEWAY_MAP.get(sn);
    }

    @Override
    public Channel getAppChannel(@NonNull String appId) {
        return APP_MAP.get(appId);
    }

    @Override
    public boolean awakeGatewayLogin(@NonNull String sn) {
        Channel channel = this.getGatewayChannel(sn);
        if (channel != null) {
            Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]已登录,无需唤醒");
            return true;
        }

        UdpInfo info = udpSessionController.info(sn);
        if (info == null) {
            Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]掉线(无UDP心跳),无法唤醒");
            return false;
        }

        int chance = 0;//try times
        while (chance < 3 && !GATEWAY_MAP.containsKey(sn)) {
            chance++;

            udpSessionController.awake(new InetSocketAddress(info.getIp(), info.getPort()));
            ThreadKit.await(Config.GATEWAY_AWAKE_CHECK_TIME);

            if (GATEWAY_MAP.containsKey(sn)) {
                return true;
            }
        }

        return GATEWAY_MAP.containsKey(sn);
    }

    @Override
    public boolean closeGatewayChannelQuietly(@NonNull String sn) {
        Channel channel = GATEWAY_MAP.remove(sn);
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        return true;
    }

}
