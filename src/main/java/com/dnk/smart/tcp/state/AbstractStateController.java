package com.dnk.smart.tcp.state;

import com.dnk.smart.tcp.cache.ChannelCacheAccessor;
import com.dnk.smart.tcp.cache.dict.Device;
import com.dnk.smart.tcp.cache.dict.LoginInfo;
import com.dnk.smart.tcp.cache.dict.State;
import io.netty.channel.Channel;
import lombok.NonNull;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_ALLOT_MIN_UDP_PORT;
import static com.dnk.smart.tcp.cache.dict.Device.GATEWAY;
import static com.dnk.smart.tcp.cache.dict.State.*;

public abstract class AbstractStateController implements StateController {
    @Resource
    private ChannelCacheAccessor channelCacheAccessor;

    @Override
    public void accept(@NonNull Channel channel) {
        if (this.turn(channel, ACCEPT)) {
            //save connect time
            channelCacheAccessor.info(channel, LoginInfo.builder().happen(System.currentTimeMillis()).build());

            this.onAccept(channel);
        }
    }

    @Override
    public void request(@NonNull Channel channel, @NonNull LoginInfo info) {
        if (this.turn(channel, REQUEST)) {
            //update info
            channelCacheAccessor.info(channel).update(info);

            this.onRequest(channel);
        }
    }

    @Override
    public void verify(@NonNull Channel channel, @NonNull String answer) {
        if (this.turn(channel, VERIFY)) {
            //verify the answer
            this.onVerify(channel, channelCacheAccessor.verifier(channel).getAnswer().equals(answer));
        }
    }

    @Override
    public void await(@NonNull Channel channel) {
        if (!this.turn(channel, AWAIT)) {
            return;
        }
        channelCacheAccessor.verifier(channel, null);
        if (channelCacheAccessor.info(channel).getDevice() == GATEWAY) {
            this.onAwait(channel);
        } else {
            //only gateway need to wait for allocate udp port
            this.success(channel, 0);
        }
    }

    /**
     * when receive the publish for gateway udp port callback it
     */
    @Override
    public void success(@NonNull Channel channel, int allocated) {
        if (this.turn(channel, SUCCESS)) {
            channelCacheAccessor.info(channel).setAllocated(allocated);
            this.onSuccess(channel);
        }
    }

    @Override
    public void close(@NonNull Channel channel) {
        channel.close();
        this.onClose(channel);
    }

    /**
     * validate current state
     */
    private boolean checkState(@NonNull Channel channel, State state) {
        if (state == null || state == CLOSED) {
            return true;
        }

        if (channelCacheAccessor.state(channel) != state) {
            return false;
        }

        LoginInfo info = channelCacheAccessor.info(channel);
        if (info == null) {
            return false;//in fact, if state != null then info != null
        }

        switch (state) {
            case ACCEPT:
            case REQUEST:
                return info.getHappen() > 0;
            case VERIFY:
                return channelCacheAccessor.verifier(channel) != null && check(info);
            case AWAIT:
                //verifier can be remove here
                return check(info);
            case SUCCESS:
                //udp port must allocated success in this step
                return (info.getDevice() != GATEWAY || info.getAllocated() >= TCP_ALLOT_MIN_UDP_PORT) && check(info);
            default:
                return false;
        }
    }

    /**
     * check after request except verifier and allocated
     */
    private boolean check(@NonNull LoginInfo info) {
        Device device = info.getDevice();
        if (device == null || info.getHappen() <= 0) {
            return false;
        }
        switch (device) {
            case APP:
                return true;
            case GATEWAY:
                return info.getSn() != null && info.getApply() >= TCP_ALLOT_MIN_UDP_PORT;
            default:
                return false;
        }
    }

    private boolean turn(@NonNull Channel channel, @NonNull State soon) {
        State current = channelCacheAccessor.state(channel);
        if (soon.previous() == current && checkState(channel, current)) {
            channelCacheAccessor.state(channel, soon);
            return true;
        }
        this.close(channel);
        return false;
    }
}
