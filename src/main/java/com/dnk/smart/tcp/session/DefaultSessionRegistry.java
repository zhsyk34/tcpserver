package com.dnk.smart.tcp.session;

import com.dnk.smart.config.Config;
import com.dnk.smart.dict.tcp.Device;
import com.dnk.smart.dict.tcp.LoginInfo;
import com.dnk.smart.dict.tcp.TcpInfo;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.tcp.awake.AwakeService;
import com.dnk.smart.tcp.cache.CacheAccessor;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.dict.tcp.Device.GATEWAY;

@Service
public class DefaultSessionRegistry implements SessionRegistry {
    private static final Map<String, Channel> ACCEPT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Channel> GATEWAY_MAP = new ConcurrentHashMap<>(Config.TCP_GATEWAY_COUNT_PREDICT);
    private static final Map<String, Channel> APP_MAP = new ConcurrentHashMap<>(Config.TCP_APP_COUNT_PREDICT);

    @Resource
    private CacheAccessor cacheAccessor;

    /**
     * 网关登录成功后广播
     */
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;

    @Resource
    private AwakeService awakeService;

    @Override
    public void registerOnActive(@NonNull Channel channel) {
        Log.logger(Factory.TCP_EVENT, "[" + cacheAccessor.ip(channel) + ":" + cacheAccessor.port(channel) + "] 连接成功");
        Channel original = ACCEPT_MAP.put(cacheAccessor.id(channel), channel);
        if (original != null) {
            original.close();
        }
    }

    @Override
    public void registerAgainBeforeLogin(@NonNull Channel channel) {
        ACCEPT_MAP.remove(cacheAccessor.id(channel));
        ACCEPT_MAP.put(cacheAccessor.info(channel).getSn(), channel);
    }

    @Override
    public void registerAfterLogin(@NonNull Channel channel) {
        String id = cacheAccessor.id(channel);
        @NonNull
        LoginInfo info = cacheAccessor.info(channel);

        Channel original;
        switch (info.getDevice()) {
            case APP:
                if (ACCEPT_MAP.remove(id) == null) {
                    Log.logger(Factory.TCP_EVENT, "app登录超时(失败)");
                    return;
                }

                original = APP_MAP.put(id, channel);
                Log.logger(Factory.TCP_EVENT, "app登录成功");

                break;
            case GATEWAY:
                String sn = info.getSn();

                if (ACCEPT_MAP.remove(sn) == null) {
                    Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]登录超时(失败)");
                    return;
                }

                original = GATEWAY_MAP.put(sn, channel);
                //注册
                cacheAccessor.registerGatewayTcpSessionInfo(TcpInfo.from(info));
                //通知
                channelMessageProcessor.publishGatewayLogin(sn, TCP_SERVER_ID);
                //TODO:执行任务

                Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]登录成功");
                break;
            default:
                return;
        }

        if (original != null) {
            Log.logger(Factory.TCP_EVENT, "重新登录,关闭原有的连接[" + cacheAccessor.info(original).getHappen() + "]");
            original.close();
        }
    }

    /**
     * channel closed yet!
     */
    @Override
    public void unRegisterAfterClose(@NonNull Channel channel) {
        String id = cacheAccessor.id(channel);
        String ip = cacheAccessor.ip(channel);
        //1.1:before login
        if (ACCEPT_MAP.remove(id) != null) {
            Log.logger(Factory.TCP_ERROR, "关闭异常连接[" + ip + "]");
            return;
        }
        @NonNull
        LoginInfo info = cacheAccessor.info(channel);
        @NonNull
        Device device = info.getDevice();
        @NonNull
        String sn = info.getSn();

        //1.2:gateway change key between verify and success
        if (device == GATEWAY && APP_MAP.remove(sn) != null) {
            Log.logger(Factory.TCP_ERROR, "关闭异常网关连接[" + sn + "]");
            return;
        }

        //2:login success!
        switch (device) {
            case APP:
                if (!APP_MAP.remove(id, channel)) {
                    Log.logger(Factory.TCP_ERROR, "app[" + ip + "]关闭出错(可能因为线时长已到被移除)");
                }
                break;
            case GATEWAY:
                if (GATEWAY_MAP.remove(sn, channel)) {
                    //注销
                    cacheAccessor.unregisterGatewayTcpSessionInfo(sn);
                    Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]下线");
                } else {
                    Log.logger(Factory.TCP_ERROR, channel.remoteAddress() + " 网关[" + sn + "]关闭出错(可能因在线时长已到被移除或重新登录时被关闭)");
                }
                break;
            default:
                Log.logger(Factory.TCP_ERROR, "关闭出错,非法的登录数据");
                break;
        }
    }

    @Override
    public Channel getAcceptChannel(@NonNull String sn) {
        return ACCEPT_MAP.get(sn);
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
    public boolean closeGatewayChannelQuietly(@NonNull String sn) {
        Channel channel = GATEWAY_MAP.remove(sn);
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        return true;
    }

}
