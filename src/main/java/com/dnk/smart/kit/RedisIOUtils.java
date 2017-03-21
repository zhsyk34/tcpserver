package com.dnk.smart.kit;

import com.dnk.smart.redis.data.dict.DataKeyEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//TODO
@Service
public class RedisIOUtils {
    @Resource
    private RedisUtils redisUtils;

    /*-----------------------------------command-----------------------------------*/

    /**
     * 调用前为 command 添加 messageId = clientId + UUID()
     */
    public void sendCommand(String sn, String command) {
        redisUtils.push(sn, command);
    }

    public void getFirstCommand(String sn) {
        redisUtils.pop(sn);
    }

    public void getAllCommand(String sn) {
        redisUtils.popAll(sn);
    }

    /*-----------------------------------udpSession-----------------------------------*/
    public String getUdpSession(String sn) {
        return redisUtils.get(DataKeyEnum.UDP_V2_SESSION.getKey(), sn);
    }

    public void setUdpSession(String sn, String udpSession) {
        redisUtils.put(DataKeyEnum.UDP_V2_SESSION.getKey(), sn, udpSession);
    }

    /*-----------------------------------tcpSession-----------------------------------*/
    public String getTcpSession(String sn) {
        return redisUtils.get(DataKeyEnum.TCP_SESSION.getKey(), sn);
    }

    public void setTcpSession(String sn, String tcpSession) {
        redisUtils.put(DataKeyEnum.TCP_SESSION.getKey(), sn, tcpSession);
    }

    /*-----------------------------------tcpServer-----------------------------------*/
    public long getTcpServerActiveTime(String serverId) {
        return Long.parseLong(redisUtils.get(DataKeyEnum.TCP_SERVER_REGISTER.getKey(), serverId));
    }

    public void setTcpServerActiveTime(String serverId) {
        redisUtils.put(DataKeyEnum.TCP_SERVER_REGISTER.getKey(), serverId, Long.toString(System.currentTimeMillis()));
    }

    /*-----------------------------------webServer-----------------------------------*/
    public long getWebServerActiveTime(String clientId) {
        return Long.parseLong(redisUtils.get(DataKeyEnum.WEB_SERVER_REGISTER.getKey(), clientId));
    }

    public void setWebServerActiveTime(String clientId) {
        redisUtils.put(DataKeyEnum.WEB_SERVER_REGISTER.getKey(), clientId, Long.toString(System.currentTimeMillis()));
    }
}
