package com.example.chatserver.domain.chat.dto;

import com.example.chatserver.domain.chat.document.ChatMessage;
import com.example.chatserver.domain.chat.document.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDTO {

    private String chatId;
    private ChatMessageType type;
    private String username;
    private String message;
    private String createdAt;

    public ChatMessageDTO(ChatMessage message) {
        this.chatId = message.getChatId();
        this.type = message.getType();
        this.username = message.getUsername();
        this.message = message.getMessage();
        this.createdAt = message.getCreatedAt();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Send {
        private String chatId;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enter {
        private String chatId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Leave {
        private String chatId;
    }
}
