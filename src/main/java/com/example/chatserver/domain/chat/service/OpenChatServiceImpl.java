package com.example.chatserver.domain.chat.service;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import com.example.chatserver.domain.chat.entity.OpenChat;
import com.example.chatserver.domain.chat.mapper.OpenChatMapper;
import com.example.chatserver.domain.chat.repository.OpenChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OpenChatServiceImpl implements OpenChatService {

    private final OpenChatRepository openChatRepository;

    @Override
    public OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, String openUsername) {
        OpenChat openChat = OpenChatMapper.toEntity(openChatDTO, openUsername);
        openChatRepository.save(openChat);

        return OpenChatMapper.toDTO(openChat, openChat.getOpenUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpenChatDTO.Info> getAllOpenChats() {
        return openChatRepository.findAll().stream()
                .map(e -> OpenChatMapper.toDTO(e, e.getOpenUsername())).toList();
    }
}
