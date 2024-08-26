package com.example.chatserver.global.security.filter;

import com.example.chatserver.global.security.service.JwtTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.chatserver.global.constant.Constants.COOKIE_AUTH_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private RedisTemplate<String, String> redisTemplate;
    private JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(req, res);
        }  catch (JwtException | IllegalArgumentException ex) {
            log.error(ex.getMessage());
            this.invalidateCookies(req, res); // 클라이언트의 쿠키(정확히는 엑세스 토큰) 삭제
            // 추가로 리프레쉬 토큰 삭제 역시 같이 갖춰야 함

            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.sendRedirect("/");
        }
    }

    private void invalidateCookies(HttpServletRequest req, HttpServletResponse res) {
        // 쿠키 전부 비우기
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_AUTH_HEADER.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    // 고민해보기: 파싱이 안되는 토큰을 바탕으로 어떻게 리프레시 토큰을 처리할 것인지?

                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setPath("/");
                    res.addCookie(cookie);
                }
            }
        }
    }
}
