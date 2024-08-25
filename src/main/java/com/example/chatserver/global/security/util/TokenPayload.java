package com.example.chatserver.global.security.util;

import com.example.chatserver.domain.user.entity.UserRoleEnum;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenPayload {
    private String sub;
    private String jti;
    private String role;
    private Date iat;
    private Date expiresAt;

    public TokenPayload(String sub, String jti, Date iat, Date expiresAt, UserRoleEnum role) {
        this.sub = sub;
        this.jti = jti;
        this.role = role.getRole();
        this.iat = iat;
        this.expiresAt = expiresAt;
    }
}
