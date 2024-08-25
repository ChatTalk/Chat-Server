package com.example.chatserver.domain.user.service;

import com.example.chatserver.domain.user.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDTO createUser(UserDTO userDto);
}
