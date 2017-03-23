package com.dnk.smart.tcp.task;

import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "of")
final class TimerTask {
    final Runnable runnable;
    final long delay;
    final long period;
    final TimeUnit unit;
}