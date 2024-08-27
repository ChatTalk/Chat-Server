package com.example.chatserver.domain.chat.service;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;

import java.util.List;

public interface OpenChatService {
    OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, String openUsername);

    List<OpenChatDTO.Info> getAllOpenChats();
}
