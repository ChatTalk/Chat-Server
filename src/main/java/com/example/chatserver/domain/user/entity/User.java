package com.example.chatserver.domain.user.entity;

import com.example.chatserver.domain.chat.entity.OpenChat;
import com.example.chatserver.domain.user.dto.UserDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRoleEnum role;

    @Column(name = "phone", nullable = false)
    private String phone;

    public User(UserDTO dto, String password) {
        this.email = dto.getEmail();
        this.password = password;
        this.role = dto.getRole();
        this.phone = dto.getPhone();
    }
}
