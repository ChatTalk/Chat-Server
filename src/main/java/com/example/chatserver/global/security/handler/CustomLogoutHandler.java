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
        String username = jwtTokenService.getUserFromToken(findAccessToken(request.getCookies(), response)).getEmail();

        // 리프레시 토큰 삭제
        redisTemplate.delete(REDIS_REFRESH_KEY + username);
    }

    private String findAccessToken(Cookie[] cookies, HttpServletResponse response){
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) {
                String token = cookie.getValue();

                // 엑세스 토큰 삭제
                cookie.setMaxAge(0);
                cookie.setValue("");
                cookie.setPath("/");
                response.addCookie(cookie);

                return token;
            }
        }
        return null;
    }
}
