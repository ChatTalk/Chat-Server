package com.example.chatserver.domain.chat.controller;

import com.example.chatserver.domain.chat.dto.OpenChatDTO;
import com.example.chatserver.domain.chat.service.OpenChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/open-chats")
public class OpenChatController {

    private final OpenChatService openChatService;

    @PostMapping("/create")
    public ResponseEntity<OpenChatDTO.Info> createOpenChat(@RequestBody OpenChatDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        OpenChatDTO.Info chatInfo = openChatService.createOpenChat(dto, userDetails.getUsername());
        return ResponseEntity.ok(chatInfo);
    }

    @GetMapping()
    public ResponseEntity<List<OpenChatDTO.Info>> getAllOpenChats() {
        List<OpenChatDTO.Info> chats = openChatService.getAllOpenChats();
        return ResponseEntity.ok(chats);
    }
}
