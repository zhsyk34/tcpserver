package com.dnk;

import com.dnk.smart.config.Config;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.tcp.TcpServer;
import com.dnk.smart.tcp.task.TaskServer;
import com.dnk.smart.udp.UdpServer;
import com.dnk.smart.util.ThreadUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Entry {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        ExecutorService service = Executors.newCachedThreadPool();

        //1:udp server
        UdpServer udpServer = context.getBean(UdpServer.class);
        service.submit(udpServer::startup);
        while (UdpServer.getChannel() == null) {
            Log.logger(Factory.UDP_EVENT, UdpServer.class.getSimpleName() + " is starting...");
            ThreadUtils.await(Config.SERVER_START_MONITOR_TIME);
        }

        //2:tcp server
        TcpServer tcpServer = context.getBean(TcpServer.class);
        service.submit(tcpServer::startup);
        while (!tcpServer.isStarted()) {
            Log.logger(Factory.TCP_EVENT, TcpServer.class.getSimpleName() + " is starting...");
            ThreadUtils.await(Config.SERVER_START_MONITOR_TIME);
        }

        service.shutdown();

        //3:task
        TaskServer taskServer = context.getBean(TaskServer.class);
        taskServer.startup();

    }
}
