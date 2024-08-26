package com.example.chatserver.domain.user.service;

import com.example.chatserver.domain.user.dto.UserDTO;
import com.example.chatserver.domain.user.entity.User;
import com.example.chatserver.domain.user.entity.UserDetailsImpl;
import com.example.chatserver.domain.user.mapper.UserMapper;
import com.example.chatserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDto) {
        if (existUserEmail(userDto.getEmail())) throw new IllegalArgumentException("이미 가입된 이메일입니다.");

        String password = passwordEncoder.encode(userDto.getPassword());
        User user = new User(userDto, password);
        userRepository.save(user);

        return UserMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserInfo(String email) {
        return UserMapper.toDTO(findUserByEmail(email));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없음. " + username)));
    }

    @Override
    public boolean existUserEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
    }
}
