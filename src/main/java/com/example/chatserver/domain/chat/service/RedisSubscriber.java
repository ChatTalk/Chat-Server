package com.example.chatserver.domain.chat.service;

import com.example.chatserver.domain.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final SimpMessageSendingOperations messagingTemplate;

    public void handleMessage(ChatMessageDTO.Info dto) {
        log.info("레디스로부터 받은 메세지: {}", dto.getMessage());
        log.info("레디스로부터 받은 메세지 보낸 사람: {}", dto.getUsername());
        log.info("레디스로부터 받은 메세지 채팅방: {}", dto.getChatId());

        // 메시지를 모든 WebSocket 클라이언트에게 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/" + dto.getChatId(), dto);
    }
}
