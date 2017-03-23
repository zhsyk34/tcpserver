package com.dnk.smart.tcp.task;

import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "of")
public class FixedTimerTask {
    private final Runnable runnable;
    private final LocalTime time;
    private final long period;
    private final TimeUnit unit;

    TimerTask toTimerTask() {
        Duration duration = Duration.between(LocalTime.now(), time);
        duration = duration.isNegative() ? duration.plus(Duration.ofDays(1)) : duration;
        //TODO
        long between = unit.convert(duration.toMillis(), TimeUnit.MILLISECONDS);

        return TimerTask.of(runnable, between, period, unit);
    }
}
