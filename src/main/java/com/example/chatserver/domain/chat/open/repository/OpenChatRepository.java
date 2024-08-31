package com.example.chatserver.domain.chat.open.repository;

import com.example.chatserver.domain.chat.open.entity.OpenChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenChatRepository extends JpaRepository<OpenChat, Long> {
}
