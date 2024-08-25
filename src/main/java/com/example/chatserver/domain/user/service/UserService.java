package com.example.chatserver.domain.user.service;

import com.example.chatserver.domain.user.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {

    UserDTO createUser(UserDTO userDto);
}
