package com.example.chatserver.global.metric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WebSocketMetricsParser {

    private final MeterRegistry meterRegistry;

    // 정규 표현식 기반 웹소켓 관련 데이터 파싱 및 메트릭 등록
    public void parseAndRegisterMetrics(String log) {
        parseAndRegisterWebSocketSessionMetrics(log);
        parseAndRegisterStompSubProtocolMetrics(log);
        parseAndRegisterChannelMetrics(log, "inboundChannel");
        parseAndRegisterChannelMetrics(log, "outboundChannel");
        parseAndRegisterChannelMetrics(log, "sockJsScheduler");
    }

    /**
     * WebSocketSession
     * 현재 세션 수 (current)
     * 총 세션 수 (total)
     * 비정상적으로 종료된 세션 수 (closed abnormally)
     * @param log
     */
    private void parseAndRegisterWebSocketSessionMetrics(String log) {
        Pattern pattern = Pattern.compile("WebSocketSession\\[(\\d+) current WS\\(\\d+\\)-HttpStream\\(\\d+\\)-HttpPoll\\(\\d+\\), (\\d+) total, (\\d+) closed abnormally");
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            int currentSessions = Integer.parseInt(matcher.group(1));
            int totalSessions = Integer.parseInt(matcher.group(2));
            int closedAbnormally = Integer.parseInt(matcher.group(3));

            meterRegistry.gauge("websocket.sessions.current", currentSessions);
            meterRegistry.gauge("websocket.sessions.total", totalSessions);
            meterRegistry.gauge("websocket.sessions.closed_abnormally", closedAbnormally);
        }
    }

    /**
     * stompSubProtocol
     * 처리된 CONNECT, CONNECTED, DISCONNECT 이벤트 수
     * @param log
     */
    private void parseAndRegisterStompSubProtocolMetrics(String log) {
        Pattern pattern = Pattern.compile("stompSubProtocol\\[processed CONNECT\\((\\d+)\\)-CONNECTED\\((\\d+)\\)-DISCONNECT\\((\\d+)\\)\\]");
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            int connectProcessed = Integer.parseInt(matcher.group(1));
            int connectedProcessed = Integer.parseInt(matcher.group(2));
            int disconnectProcessed = Integer.parseInt(matcher.group(3));

            meterRegistry.gauge("stomp.connect.processed", connectProcessed);
            meterRegistry.gauge("stomp.connected.processed", connectedProcessed);
            meterRegistry.gauge("stomp.disconnect.processed", disconnectProcessed);
        }
    }

    /**
     * inboundChannel, outboundChannel, sockJsScheduler
     * 풀 사이즈 (pool size)
     * 활성 스레드 수 (active threads)
     * 대기 중인 작업 수 (queued tasks)
     * 완료된 작업 수 (completed tasks)
     * @param log
     * @param channelType
     */
    private void parseAndRegisterChannelMetrics(String log, String channelType) {
        Pattern pattern = Pattern.compile(channelType + "\\[pool size = (\\d+), active threads = (\\d+), queued tasks = (\\d+), completed tasks = (\\d+)\\]");
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            int poolSize = Integer.parseInt(matcher.group(1));
            int activeThreads = Integer.parseInt(matcher.group(2));
            int queuedTasks = Integer.parseInt(matcher.group(3));
            int completedTasks = Integer.parseInt(matcher.group(4));

            meterRegistry.gauge(channelType + ".pool.size", poolSize);
            meterRegistry.gauge(channelType + ".active.threads", activeThreads);
            meterRegistry.gauge(channelType + ".queued.tasks", queuedTasks);
            meterRegistry.gauge(channelType + ".completed.tasks", completedTasks);
        }
    }
}
