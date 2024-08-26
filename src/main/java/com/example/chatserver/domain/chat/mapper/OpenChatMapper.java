package com.example.chatserver.domain.chat.mapper;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import com.example.chatserver.domain.chat.entity.OpenChat;
import com.example.chatserver.domain.user.entity.User;

public class OpenChatMapper {

    public static OpenChat toEntity(OpenChatDTO dto, User user) {
        return new OpenChat(dto, user);
    }

    public static OpenChatDTO.Info toDTO(OpenChat chat, String username) {
        return new OpenChatDTO.Info(chat.getTitle(), username, chat.getMaxPersonnel());
    }
}
