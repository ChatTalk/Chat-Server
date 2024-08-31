package com.example.chatserver.domain.chat.message.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisMessageListener redisMessageListener;

    // 채널 구독 메서드
    public void subscribe(String channel) {
        redisMessageListenerContainer.addMessageListener(
                redisMessageListener, new ChannelTopic(channel));
        log.info("채널 '{}' 구독", channel);
    }

    // 채널 해제 메서드
    public void unsubscribe(String channel) {
        redisMessageListenerContainer.removeMessageListener(
                redisMessageListener, new ChannelTopic(channel));
        log.info("채널 '{}' 해제", channel);
    }
}
