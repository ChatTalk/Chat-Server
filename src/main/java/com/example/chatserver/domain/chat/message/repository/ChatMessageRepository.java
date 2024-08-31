package com.example.chatserver.domain.chat.message.repository;

import com.example.chatserver.domain.chat.message.document.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
