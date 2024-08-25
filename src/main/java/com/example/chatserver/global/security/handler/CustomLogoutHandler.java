package com.example.chatserver.global.security.handler;

import com.example.chatserver.global.security.service.JwtTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.example.chatserver.global.constant.Constants.COOKIE_AUTH_HEADER;
import static com.example.chatserver.global.constant.Constants.REDIS_REFRESH_KEY;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenService jwtTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String username = jwtTokenService.getUserFromToken(findAccessToken(request.getCookies())).getEmail();

        redisTemplate.delete(REDIS_REFRESH_KEY + username);
    }

    private String findAccessToken(Cookie[] cookies){
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) return cookie.getValue();
        }
        return null;
    }
}
