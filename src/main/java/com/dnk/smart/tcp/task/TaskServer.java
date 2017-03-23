package com.dnk.smart.tcp.task;

import com.dnk.smart.tcp.awake.AwakeService;
import com.dnk.smart.tcp.session.SessionRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public final class TaskServer {
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private AwakeService awakeService;

    @PostConstruct
    public void execute() {
        loopTask();
        timeTask();
    }

    private void loopTask() {
        List<LoopTask> loopTasks = new ArrayList<>();
        List<LoopTask> sessionTasks = sessionRegistry.monitor();
        LoopTask awakeTask = awakeService.monitor();

        loopTasks.addAll(sessionTasks);
        loopTasks.add(awakeTask);

        ExecutorService service = Executors.newFixedThreadPool(loopTasks.size());
        loopTasks.forEach(task -> service.submit(() -> {
            while (true) {
                task.execute();
            }
        }));
        service.shutdown();
    }

    private void timeTask() {
        List<TimerTask> timerTasks = new ArrayList<>();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(timerTasks.size());
        timerTasks.forEach(task -> service.scheduleAtFixedRate(task.runnable, task.delay, task.period, task.unit));
    }

}
