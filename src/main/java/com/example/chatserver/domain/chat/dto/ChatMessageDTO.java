package com.example.chatserver.domain.chat.dto;

import com.example.chatserver.domain.chat.document.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDTO {
    private String chatId;
    private String username;
    private String message;
    private String createdAt;

    public ChatMessageDTO(ChatMessage mesage) {
        this.chatId = mesage.getChatId();
        this.username = mesage.getUsername();
        this.message = mesage.getMessage();
        this.createdAt = mesage.getCreatedAt();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Send {
        private String chatId;
        private String message;
    }
}
