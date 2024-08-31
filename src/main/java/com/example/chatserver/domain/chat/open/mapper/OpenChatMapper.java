package com.example.chatserver.domain.chat.open.mapper;

import com.example.chatserver.domain.chat.open.dto.OpenChatDTO;
import com.example.chatserver.domain.chat.open.entity.OpenChat;

public class OpenChatMapper {

    public static OpenChat toEntity(OpenChatDTO dto, String openUsername) {
        return new OpenChat(dto, openUsername);
    }

    public static OpenChatDTO.Info toDTO(OpenChat chat, String username) {
        return new OpenChatDTO.Info(chat.getId().toString(), chat.getTitle(), username, chat.getMaxPersonnel());
    }
}
