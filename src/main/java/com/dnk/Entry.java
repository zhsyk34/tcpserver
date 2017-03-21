package com.dnk;

import com.dnk.smart.udp.UdpServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Entry {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        UdpServer udpServer = context.getBean(UdpServer.class);
        udpServer.start();
    }
}
