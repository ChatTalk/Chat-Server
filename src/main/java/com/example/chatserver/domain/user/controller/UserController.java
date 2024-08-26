package com.example.chatserver.domain.user.controller;

import com.example.chatserver.domain.user.dto.UserDTO;
import com.example.chatserver.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        log.info("회원가입 시도 이메일: {}", userDTO.getEmail());
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.OK);
    }
}
