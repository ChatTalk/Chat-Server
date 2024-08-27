package com.example.chatserver.domain.chat.service;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;

public interface OpenChatService {
    OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, String openUsername);
}
