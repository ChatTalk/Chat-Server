package com.example.chatserver.domain.chat.open.service;

import com.example.chatserver.domain.chat.open.dto.OpenChatDTO;

import java.util.List;

public interface OpenChatService {
    OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, String openUsername);

    List<OpenChatDTO.Info> getAllOpenChats();
}
