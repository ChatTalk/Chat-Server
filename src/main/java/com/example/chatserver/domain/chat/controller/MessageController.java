package com.example.chatserver.domain.chat.controller;

import com.example.chatserver.domain.chat.document.ChatMessage;
import com.example.chatserver.domain.chat.dto.ChatMessageDTO;
import com.example.chatserver.domain.chat.service.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static com.example.chatserver.global.constant.Constants.REDIS_CHAT_PREFIX;
import static com.example.chatserver.global.constant.Constants.REDIS_SUBSCRIBE_KEY;

@Slf4j
@Controller
public class MessageController {

    private final RedisTemplate<String, ChatMessageDTO> messageTemplate;
    private final RedisTemplate<String, String> subscribeRedisTemplate;
    private final RedisSubscriber redisSubscriber;

    public MessageController(
            RedisTemplate<String, ChatMessageDTO> messageTemplate,
            @Qualifier("subscribeTemplate") RedisTemplate<String, String> subscribeRedisTemplate,
            RedisSubscriber redisSubscriber) {
        this.messageTemplate = messageTemplate;
        this.subscribeRedisTemplate = subscribeRedisTemplate;
        this.redisSubscriber = redisSubscriber;
    }

    // 사용자의 채팅방 입장
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDTO.Enter enter, Principal principal) {
        log.info("{}번 채팅방에서 클라이언트로부터 {} 회원이 입장 요청",
                enter.getChatId(), principal.getName());

        // 중복 재구독 방지용 코드
        Boolean isMember = subscribeRedisTemplate.opsForSet()
                .isMember(REDIS_SUBSCRIBE_KEY + enter.getChatId(), principal.getName());

        if (Boolean.TRUE.equals(isMember)) {
            log.warn(
                    "회원 {}이 이미 구독된 채팅 {}번에 재구독을 수행하려고 함",
                    principal.getName(), enter.getChatId());
            return;
        }

        // 채팅방 구독
        redisSubscriber.subscribe(REDIS_CHAT_PREFIX + enter.getChatId());
        subscribeRedisTemplate.opsForSet().add(REDIS_SUBSCRIBE_KEY + enter.getChatId(), principal.getName());

        // 채팅 메세지 엔티티 관련 서비스 로직(mongoDB) 수행
        ChatMessage message = new ChatMessage(enter, principal.getName());

        ChatMessageDTO dto = new ChatMessageDTO(message);
        messageTemplate.convertAndSend(REDIS_CHAT_PREFIX + message.getChatId(), dto);
    }

    // 사용자의 메세지 입력 송수신
    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO.Send send, Principal principal) {
        log.info("{}반 채팅방에서 클라이언트로부터 {} 회원이 메세지 전송 요청: {}",
                send.getChatId(), principal.getName(), send.getMessage());

        // 채팅 메세지 엔티티 관련 서비스 로직(mongoDB) 수행
        ChatMessage message = new ChatMessage(send, principal.getName());

        ChatMessageDTO dto = new ChatMessageDTO(message);
        messageTemplate.convertAndSend(REDIS_CHAT_PREFIX + message.getChatId(), dto);
    }

    // 사용자의 채팅방 퇴장
    @MessageMapping(value = "/chat/leave")
    public void leave(ChatMessageDTO.Leave leave, Principal principal) {
        log.info("{}반 채팅방에서 클라이언트로부터 {} 회원이 퇴장 요청",
                leave.getChatId(), principal.getName());

        // 채팅 메세지 엔티티 관련 서비스 로직(mongoDB) 수행
        ChatMessage message = new ChatMessage(leave, principal.getName());

        ChatMessageDTO dto = new ChatMessageDTO(message);
        messageTemplate.convertAndSend(REDIS_CHAT_PREFIX + message.getChatId(), dto);

        // 메세지 전송 완료 후, 채팅방 구독 해제
        redisSubscriber.unsubscribe(REDIS_CHAT_PREFIX + leave.getChatId());
        subscribeRedisTemplate.opsForSet().remove(REDIS_SUBSCRIBE_KEY + leave.getChatId(), principal.getName());
    }
}
