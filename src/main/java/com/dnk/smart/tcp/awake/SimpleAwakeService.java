package com.dnk.smart.tcp.awake;

import com.dnk.smart.config.Config;
import com.dnk.smart.tcp.message.publish.ServerMessageProcessor;
import com.dnk.smart.tcp.task.LoopTask;
import com.dnk.smart.udp.session.UdpSessionController;
import com.dnk.smart.util.TimeUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public final class SimpleAwakeService implements AwakeService {

    private static final Map<String, Record> GATEWAY_MAP = new ConcurrentHashMap<>();

    @Resource
    private UdpSessionController udpSessionController;
    @Resource
    private ServerMessageProcessor serverMessageProcessor;

    @Override
    public void execute(@NonNull String sn) {
        GATEWAY_MAP.put(sn, new Record());
    }

    @Override
    public void cancel(@NonNull String sn) {
        GATEWAY_MAP.remove(sn);
    }

    @Override
    public LoopTask monitor() {
        return () -> {
            for (Map.Entry<String, Record> entry : GATEWAY_MAP.entrySet()) {
                String sn = entry.getKey();
                Record record = entry.getValue();

                if (record.getCount() > Config.GATEWAY_AWAKE_TIME) {
                    serverMessageProcessor.publishGatewayAwakeFail(sn, Config.TCP_SERVER_ID);
                    continue;
                }

                if (TimeUtils.timeout(record.getTime(), Config.GATEWAY_AWAKE_STEP)) {
                    record.count++;
                    record.time = System.currentTimeMillis();
                    if (!udpSessionController.awake(sn)) {
                        serverMessageProcessor.publishGatewayAwakeFail(sn, Config.TCP_SERVER_ID);
                    }
                }
            }
        };
    }

    @Getter
    @Setter
    private static class Record {
        private int count;
        private long time;
    }
}
