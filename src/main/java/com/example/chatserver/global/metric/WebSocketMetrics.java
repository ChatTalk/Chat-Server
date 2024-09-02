package com.example.chatserver.global.metric;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Slf4j
@Component
@RequiredArgsConstructor
// 스프링 내의 모든 빈들이 올라가고 실행시키기 위한 ContextRefreshedEvent 제네릭 할당
// 그 이벤트가 발생했을 때의 동작(웹소켓 관련 메트릭 수집)을 위한 ApplicationListener 구현
public class WebSocketMetrics implements ApplicationListener<ContextRefreshedEvent> {

    private final WebSocketMessageBrokerStats webSocketMessageBrokerStats;
    private final WebSocketMetricsParser webSocketMetricsParser;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("WebSocketMetrics started");
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void collectMetrics() {
        String webSocketLog = webSocketMessageBrokerStats.toString();
        log.info("테스트: {}", webSocketLog);
        webSocketMetricsParser.parseAndRegisterMetrics(webSocketLog);
    }
}
