package com.example.chatserver.domain.chat.controller;

import com.example.chatserver.domain.chat.document.ChatMessage;
import com.example.chatserver.domain.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO.Send send, Principal principal) {
        log.info("{}반 채팅방에서 클라이언트로부터 {} 회원이 메세지 전송: {}",
                send.getChatId(), principal.getName(), send.getMessage());

        ChatMessageDTO.Info message = new ChatMessageDTO.Info(
                send.getChatId(), principal.getName(), send.getMessage());

        // 채팅 메세지 엔티티 관련 서비스 로직(mongoDB) 수행
        ChatMessage chatMessage = new ChatMessage(message);
        redisTemplate.convertAndSend(message.getChatId(), message);
    }
}
