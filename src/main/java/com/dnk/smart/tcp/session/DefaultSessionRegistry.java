package com.dnk.smart.tcp.session;

import com.dnk.smart.config.Config;
import com.dnk.smart.dict.redis.cache.Command;
import com.dnk.smart.dict.redis.cache.TcpSessionData;
import com.dnk.smart.dict.tcp.Device;
import com.dnk.smart.dict.tcp.LoginInfo;
import com.dnk.smart.dict.tcp.State;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.tcp.cache.CacheAccessor;
import com.dnk.smart.tcp.command.CommandProcessor;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import com.dnk.smart.tcp.state.StateController;
import com.dnk.smart.tcp.task.LoopTask;
import com.dnk.smart.util.ThreadUtils;
import com.dnk.smart.util.TimeUtils;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;

@Service
public class DefaultSessionRegistry implements SessionRegistry {
    private static final Map<String, Channel> ACCEPT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Channel> APP_MAP = new ConcurrentHashMap<>(Config.TCP_APP_COUNT_PREDICT);
    private static final Map<String, Channel> GATEWAY_MAP = new ConcurrentHashMap<>(Config.TCP_GATEWAY_COUNT_PREDICT);

    /**
     * 网关登录后在redisServer上登记
     */
    @Resource
    private CacheAccessor cacheAccessor;

    /**
     * 超时网关被强制关闭后进行相应处理
     *
     * @see #unRegisterAfterClose(Channel)
     */
    @Resource
    private StateController stateController;

    /**
     * 网关登录成功后在tcpServer广播
     */
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;

    /**
     * 网关登录成功后立即尝试执行任务
     * 网关关闭连接时尝试取消所有任务
     */
    @Resource
    private CommandProcessor commandProcessor;

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
        @NonNull
        LoginInfo info = cacheAccessor.info(channel);

        Channel original;
        switch (info.getDevice()) {
            case APP:
                String id = cacheAccessor.id(channel);

                if (!ACCEPT_MAP.remove(id, channel)) {
                    Log.logger(Factory.TCP_EVENT, "app[" + cacheAccessor.ip(channel) + "]登录超时(失败)");
                    return;
                }

                original = APP_MAP.put(id, channel);
                Log.logger(Factory.TCP_EVENT, "app登录成功");

                break;
            case GATEWAY:
                String sn = info.getSn();

                if (!ACCEPT_MAP.remove(sn, channel)) {
                    Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]登录超时(失败)");
                    return;
                }

                original = GATEWAY_MAP.put(sn, channel);

                TcpSessionData sessionData = new TcpSessionData(TCP_SERVER_ID, sn, cacheAccessor.ip(channel), cacheAccessor.port(channel), info.getApply(), info.getAllocated(), info.getHappen());
                cacheAccessor.registerGatewayTcpSessionInfo(sessionData);

                Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]登录成功");

                channelMessageProcessor.publishGatewayLogin(sn, TCP_SERVER_ID);

                commandProcessor.startup(channel);

                break;
            default:
                return;
        }

        if (original != null) {
            String time = TimeUtils.format(TimeUtils.fromMillisecond(cacheAccessor.info(original).getHappen()));
            Log.logger(Factory.TCP_EVENT, "重新登录,关闭原有的连接[" + time + "]");
            original.close();
        }
    }

    /**
     * 连接已关闭:TODO
     * 为防止其它事件...
     */
    @Override
    public void unRegisterAfterClose(@NonNull Channel channel) {
        State state = cacheAccessor.state(channel);

        String id = cacheAccessor.id(channel);
        @NonNull
        LoginInfo info = cacheAccessor.info(channel);
        Device device = info.getDevice();
        String sn = info.getSn();

        if (state == null) {
            return;//未进行登记,直接关闭
        }

        switch (state) {
            case ACCEPT:
            case REQUEST:
            case VERIFY://分配端口前
                if (ACCEPT_MAP.remove(id, channel)) {
                    Log.logger(Factory.TCP_EVENT, "连接[" + cacheAccessor.ip(channel) + "]超时未登录已被注销");
                }
                break;
            case AWAIT://即将分配端口
                switch (device) {
                    case APP:
                        if (ACCEPT_MAP.remove(id, channel)) {
                            Log.logger(Factory.TCP_EVENT, "app[" + cacheAccessor.ip(channel) + "]超时未登录已被注销");
                        }
                        break;
                    case GATEWAY:
                        if (ACCEPT_MAP.remove(sn, channel)) {
                            Log.logger(Factory.TCP_EVENT, "网关[" + sn + "]超时未登录已被注销");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case SUCCESS://已登录的网关在正常关闭前为改变为下一个状态:CLOSED
            case CLOSED:
                switch (device) {
                    case APP:
                        if (!APP_MAP.remove(id, channel)) {
                            Log.logger(Factory.TCP_ERROR, "app[" + cacheAccessor.ip(channel) + "]关闭出错(可能因为线时长已到被移除)");
                        }
                        break;
                    case GATEWAY:
                        if (GATEWAY_MAP.remove(sn, channel)) {
                            cacheAccessor.unregisterGatewayTcpSessionInfo(sn);//logout

                            commandProcessor.clean(channel);//cancel all received request command

                            Log.logger(Factory.TCP_EVENT, "gateway[" + sn + "]下线,清空任务队列...");
                        } else {
                            Log.logger(Factory.TCP_ERROR, channel.remoteAddress() + " gateway[" + sn + "]关闭出错(可能因在线时长已到被移除或重新登录时被关闭)");
                        }
                        break;
                    default:
                        Log.logger(Factory.TCP_ERROR, "关闭出错,非法的登录数据");
                        break;
                }
            default:
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

    @Override
    public List<LoopTask> monitor() {
        final AtomicInteger count1 = new AtomicInteger();
        final AtomicInteger count2 = new AtomicInteger();
        final AtomicInteger count3 = new AtomicInteger();
        final int f = 10;

        LoopTask acceptTask = () -> {
            //TODO:
            if (count1.getAndIncrement() % f == 0) {
                Log.logger(Factory.TCP_EVENT, "未登录连接数:[" + ACCEPT_MAP.size() + "]");
            }

            ACCEPT_MAP.forEach((key, channel) -> {
                if (TimeUtils.timeout(cacheAccessor.info(channel).getHappen(), Config.TCP_LOGIN_TIMEOUT, TimeUnit.SECONDS)) {
                    Log.logger(Factory.TCP_EVENT, "超时未登录,移除");
                    stateController.close(channel);
                }
            });

            ThreadUtils.await(1000);
        };

        LoopTask appTask = () -> {
            if (count2.getAndIncrement() % f == 0) {
                Log.logger(Factory.TCP_EVENT, "app在线:[" + APP_MAP.size() + "]");
            }

            APP_MAP.forEach((id, channel) -> {
                if (TimeUtils.timeout(cacheAccessor.info(channel).getHappen(), Config.TCP_APP_TIMEOUT, TimeUnit.SECONDS)) {
                    Log.logger(Factory.TCP_EVENT, "app在线时长已到,移除!");
                    stateController.close(channel);
                }
            });
            ThreadUtils.await(1000);
        };

        LoopTask gatewayTask = () -> {
            if (count3.getAndIncrement() % f == 0) {
                Log.logger(Factory.TCP_EVENT, "gateway在线:[" + GATEWAY_MAP.size() + "]");
            }

            GATEWAY_MAP.forEach((sn, channel) -> {
                if (TimeUtils.timeout(cacheAccessor.info(channel).getHappen(), Config.TCP_GATEWAY_TIMEOUT, TimeUnit.SECONDS)) {
                    Log.logger(Factory.TCP_EVENT, "gateway[" + cacheAccessor.info(channel).getSn() + "]在线时长已到,移除");
                    stateController.close(channel);//not remove from map here!!
                }
            });

            ThreadUtils.await(1000);
        };

        LoopTask commandTask = () -> GATEWAY_MAP.forEach((sn, channel) -> {
            Command command = cacheAccessor.command(channel);
            if (command != null && TimeUtils.timeout(command.getRuntime(), Config.TCP_COMMAND_TIMEOUT, TimeUnit.SECONDS)) {
                Log.logger(Factory.TCP_ERROR, "gateway[" + cacheAccessor.info(channel).getSn() + "]响应超时,关闭!");
                stateController.close(channel);
            }
        });

        return Collections.unmodifiableList(Arrays.asList(acceptTask, appTask, gatewayTask, commandTask));
    }

}
