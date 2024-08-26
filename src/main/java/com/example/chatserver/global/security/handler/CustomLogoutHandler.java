package com.example.chatserver.global.security.handler;

import com.example.chatserver.global.security.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static com.example.chatserver.global.constant.Constants.COOKIE_AUTH_HEADER;
import static com.example.chatserver.global.constant.Constants.REDIS_REFRESH_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenService jwtTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("로그아웃 핸들러 작동");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) {
                String decodedToken = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                try {
                    log.info("정상 토큰에서의 로그아웃 처리");
                    String email = jwtTokenService.getUserFromToken(decodedToken.substring(7)).getEmail();
                    redisTemplate.delete(REDIS_REFRESH_KEY + email);
                } catch (ExpiredJwtException e) {
                    log.warn("만료 토큰에서의 로그아웃 처리");
                    String email = jwtTokenService.getUsernameFromExpiredJwt(e);
                    redisTemplate.delete(REDIS_REFRESH_KEY + email);
                } catch (Exception e) {
                    log.error("Redis에서 리프레시 토큰 삭제 중 오류 발생", e);
                    throw e;
                }
                break;
            }
        }
    }
}
