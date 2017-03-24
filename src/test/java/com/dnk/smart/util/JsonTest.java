package com.dnk.smart.util;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.dict.tcp.TcpInfo;
import org.junit.Test;

public class JsonTest {

    @Test
    public void parse() throws Exception {
        String s = JSON.toJSONString(new TcpInfo("a", 3000, 50001, 2));
        TcpInfo info = JSON.parseObject(s, TcpInfo.class);
        System.out.println(info);

    }
}
