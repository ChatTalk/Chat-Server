package com.example.chatserver.domain.chat.message.service;

import com.example.chatserver.domain.chat.message.dto.ChatMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import static com.example.chatserver.global.constant.Constants.CHAT_DESTINATION;

// 메세지를 전파하는 클래스
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisMessageListener implements MessageListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        try {
            ChatMessageDTO dto = new ObjectMapper().readValue(body, ChatMessageDTO.class);

            log.info("레디스 채널로부터 전해받은 메세지 '{}'에서 '{}'가 : {}",
                    channel, dto.getUsername(), dto.getMessage());

            // 메시지를 모든 WebSocket 클라이언트에게 브로드캐스트
            messagingTemplate.convertAndSend(
                    CHAT_DESTINATION + dto.getChatId(), dto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
