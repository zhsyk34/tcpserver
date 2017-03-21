package com.dnk.smart.kit;

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
}