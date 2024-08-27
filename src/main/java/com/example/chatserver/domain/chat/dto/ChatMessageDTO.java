package com.example.chatserver.domain.chat.dto;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Send {
        private String chatId;
        private String message;
    }
}
