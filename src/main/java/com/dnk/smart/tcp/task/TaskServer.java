package com.dnk.smart.tcp.task;

import com.dnk.smart.config.Config;
import com.dnk.smart.tcp.awake.AwakeService;
import com.dnk.smart.tcp.cache.CacheAccessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import com.dnk.smart.util.ThreadUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public final class TaskServer {
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private AwakeService awakeService;
    @Resource
    private CacheAccessor accessor;

    @PostConstruct
    public void execute() {
        startup();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startup() {
        List<LoopTask> loopTasks = loopTasks();
        ExecutorService service = Executors.newFixedThreadPool(loopTasks.size());
        loopTasks.forEach(task -> service.submit(() -> {
            while (true) {
                task.run();
                ThreadUtils.await(1);
            }
        }));
        service.shutdown();

        List<TimerTask> timerTasks = timerTasks();
        ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(timerTasks.size());
        timerTasks.forEach(task -> scheduledService.scheduleAtFixedRate(task.runnable, task.delay, task.period, task.unit));
    }

    private List<LoopTask> loopTasks() {
        List<LoopTask> list = new ArrayList<>();
        List<LoopTask> sessionTasks = sessionRegistry.monitor();
        LoopTask awakeTask = awakeService.monitor();

        list.addAll(sessionTasks);
        list.add(awakeTask);

        return list;
    }

    private List<TimerTask> timerTasks() {
        List<TimerTask> list = new ArrayList<>();
        list.add(TimerTask.of(() -> accessor.reportServerStatus(Config.TCP_SERVER_ID), 0, 5, TimeUnit.MINUTES));
        return list;
    }

}
