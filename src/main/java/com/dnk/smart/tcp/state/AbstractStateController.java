package com.dnk.smart.tcp.state;

import com.dnk.smart.dict.tcp.Device;
import com.dnk.smart.dict.tcp.LoginInfo;
import com.dnk.smart.dict.tcp.State;
import com.dnk.smart.tcp.cache.CacheAccessor;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_ALLOT_MIN_UDP_PORT;
import static com.dnk.smart.dict.tcp.Device.GATEWAY;
import static com.dnk.smart.dict.tcp.State.*;

public abstract class AbstractStateController implements StateController {
    @Resource
    private CacheAccessor cacheAccessor;

    @Override
    public void accept(@NonNull Channel channel) {
        if (this.turn(channel, ACCEPT)) {
            //save connect time
            cacheAccessor.info(channel, LoginInfo.builder().happen(System.currentTimeMillis()).build());

            this.onAccept(channel);
        }
    }

    @Override
    public void request(@NonNull Channel channel, @NonNull LoginInfo info) {
        if (this.turn(channel, REQUEST)) {
            //update info
            cacheAccessor.info(channel).update(info);

            this.onRequest(channel);
        }
    }

    @Override
    public void verify(@NonNull Channel channel, @NonNull String answer) {
        if (this.turn(channel, VERIFY)) {
            //verify the answer
            this.onVerify(channel, cacheAccessor.verifier(channel).getAnswer().equals(answer));
        }
    }

    @Override
    public void await(@NonNull Channel channel) {
        if (!this.turn(channel, AWAIT)) {
            return;
        }

        cacheAccessor.verifier(channel, null);//clean unused data

        if (cacheAccessor.info(channel).getDevice() == GATEWAY) {
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
            cacheAccessor.info(channel).setAllocated(allocated);
            this.onSuccess(channel);
        }
    }

    @Override
    public void close(@NonNull Channel channel) {
        channel.close();
        cacheAccessor.state(channel, CLOSED);
        this.onClose(channel);
    }

    /**
     * validate current state info
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean checkState(@NonNull Channel channel) {
        State state = cacheAccessor.state(channel);
        if (state == null) {
            return true;
        }

        if (cacheAccessor.state(channel) != state) {
            return false;
        }

        LoginInfo info = cacheAccessor.info(channel);
        if (info == null) {
            return false;//in fact, if state != null then info != null
        }

        switch (state) {
            case ACCEPT:
                return info.getHappen() > 0;
            case REQUEST:
                return checkInfo(info);
            case VERIFY:
                return cacheAccessor.verifier(channel) != null && checkInfo(info);
            case AWAIT:
                return checkInfo(info);//verifier can be remove here
            case SUCCESS:
                //udp port must allocated success in this step if device = gateway
                return (info.getDevice() != GATEWAY || info.getAllocated() >= TCP_ALLOT_MIN_UDP_PORT) && checkInfo(info);
            default:
                return false;
        }
    }

    /**
     * checkInfo after request except verifier and allocated
     */
    private boolean checkInfo(@NonNull LoginInfo info) {
        Device device = info.getDevice();
        if (device == null || info.getHappen() <= 0 || StringUtils.isEmpty(info.getSn())) {
            return false;
        }

        switch (device) {
            case APP:
                return true;
            case GATEWAY:
                return info.getApply() >= TCP_ALLOT_MIN_UDP_PORT;
            default:
                return false;
        }
    }

    private boolean turn(@NonNull Channel channel, @NonNull State soon) {
        if (soon.previous() == cacheAccessor.state(channel) && checkState(channel)) {
            cacheAccessor.state(channel, soon);
            return true;
        }

        this.close(channel);
        return false;
    }
}
