package com.example.chatserver.domain.user.dto;

import com.example.chatserver.domain.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    @Pattern(regexp ="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
    private String email;

    @NotBlank(message = "제목을 입력해주세요.")
    private String password;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
    private String phone;

    private UserRoleEnum role;
}
