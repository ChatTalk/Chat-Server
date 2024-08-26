package com.example.chatserver.domain.chat.mapper;

import com.example.chatserver.domain.chat.document.ChatMessage;
import com.example.chatserver.domain.chat.dto.ChatMessageDTO;

public class ChatMessageMapper {
    public static ChatMessage toChatMessage(ChatMessageDTO dto) {
        return new ChatMessage(dto);
    }

    public static ChatMessageDTO toChatMessageDTO(ChatMessage chatMessage) {
        return new ChatMessageDTO(
                chatMessage.getChatId(),
                chatMessage.getUsername(),
                chatMessage.getMessage(),
                chatMessage.getCreatedAt()
        );
    }
}
