package com.dnk.smart.tcp.awake;

import com.dnk.smart.tcp.task.LoopTask;
import lombok.NonNull;

public interface AwakeService {

    void append(@NonNull String sn);

    void cancel(@NonNull String sn);

    LoopTask monitor();
}
