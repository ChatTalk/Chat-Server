package com.example.chatserver.domain.chat.service;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import com.example.chatserver.domain.chat.entity.OpenChat;
import com.example.chatserver.domain.chat.mapper.OpenChatMapper;
import com.example.chatserver.domain.chat.repository.OpenChatRepository;
import com.example.chatserver.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OpenChatServiceImpl implements OpenChatService {

    private final OpenChatRepository openChatRepository;

    @Override
    public OpenChatDTO.Info createOpenChat(OpenChatDTO openChatDTO, User user) {
        OpenChat openChat = OpenChatMapper.toEntity(openChatDTO, user);
        openChatRepository.save(openChat);

        return OpenChatMapper.toDTO(openChat, openChat.getOpenUser().getEmail());
    }
}
