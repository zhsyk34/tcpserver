package com.dnk.smart.util;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.config.Config;
import com.dnk.smart.dict.redis.RedisChannel;
import com.dnk.smart.dict.redis.channel.GatewayUdpPortAllocateData;
import com.dnk.smart.dict.redis.channel.WebCommandRequestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring.xml")
public class RedisUtilsTest {

    @Resource
    private RedisTemplate redisTemplate;
    private String key = "crg";

    @Test
    public void add() throws Exception {
        for (int i = 1; i < 5; i++) {
            redisTemplate.opsForHash().put(key, "k" + i, "v" + i);
        }
    }

    @Test
    public void remove() throws Exception {
        System.out.println(redisTemplate.opsForHash().delete(key, "k3"));

    }

    @Test
    public void time() throws Exception {
        System.out.println(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        System.out.println(new Date().getTime());

    }

    @Test
    public void channelName() throws Exception {
        System.out.println(RedisChannel.WEB_COMMAND_REQUEST.channel());

    }

    @Test
    public void publish() throws Exception {
        String message = JSON.toJSONString(WebCommandRequestData.of(Config.TCP_SERVER_ID, "3-1-1-1"));
        GatewayUdpPortAllocateData.of("2-1-1-1", 50003);
        System.out.println(message);
        redisTemplate.convertAndSend(RedisChannel.WEB_COMMAND_REQUEST.channel(), message);

    }

    @Test
    public void port() throws Exception {
        String message = JSON.toJSONString(GatewayUdpPortAllocateData.of("2-1-1-1", 50003));
        redisTemplate.convertAndSend(RedisChannel.GATEWAY_UDP_PORT_ALLOCATE.channel(), message);

    }
}