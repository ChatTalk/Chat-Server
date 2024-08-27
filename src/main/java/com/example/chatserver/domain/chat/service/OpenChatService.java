package com.example.chatserver.domain.chat.service;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import com.example.chatserver.domain.user.entity.User;

public interface OpenChatService {
    OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, User user);
}
