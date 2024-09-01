package com.example.chatserver.global.metric;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketMetrics {

    // 현재 활성화된 웹소켓 연결 수를 추적
    private final AtomicInteger activeConnections;
    // 총 송수신된 메시지 수를 추적
    private final Counter messageCounter;

    public WebSocketMetrics(MeterRegistry meterRegistry) {
        // 현재 활성화된 연결 수를 카운트하는 Gauge
        this.activeConnections = meterRegistry.gauge("websocket.active.connections", new AtomicInteger(0));

        // 송수신 메시지 수를 카운트하는 Counter
        this.messageCounter = meterRegistry.counter("websocket.messages.total");
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        activeConnections.incrementAndGet();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        activeConnections.decrementAndGet();
    }

    public void incrementMessageCounter() {
        messageCounter.increment();
    }
}
