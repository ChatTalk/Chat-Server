package com.example.chatserver.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OpenChatDTO {
    private String title;
    private Integer maxPersonnel;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private String chatId;
        private String title;
        private String openUsername;
        private Integer maxPersonnel;
    }
}
