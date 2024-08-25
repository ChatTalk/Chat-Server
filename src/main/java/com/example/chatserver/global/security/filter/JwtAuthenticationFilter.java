package com.example.chatserver.global.security.filter;

import com.example.chatserver.domain.user.entity.UserRoleEnum;
import com.example.chatserver.domain.user.service.UserService;
import com.example.chatserver.global.security.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.example.chatserver.global.constant.Constants.COOKIE_AUTH_HEADER;

@RequiredArgsConstructor
@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("인증 시도");
        String beforeToken = findAccessToken(request.getCookies());

        try {
            // 엑세스토큰 유효기간 만료시 바로 JwtException 발생
            // 그로 인해 JwtException 필터에서 곧바로 로그인 화면으로 내보내는 것
            // 즉, 엑세스토큰 유효기간 만료시, 리프레쉬 토큰을 기반으로 한 재발급 절차 추가가 필요

            if(beforeToken == null) throw new JwtException("엑세스 토큰이 존재하지 않습니다.");

            String accessToken = jwtTokenService.validAccessToken(beforeToken);
            String tokenValue = jwtTokenService.extractValue(accessToken);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createAuthentication(jwtTokenService.getUserFromToken(tokenValue).getEmail()));
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            String username = jwtTokenService.getUsernameFromExpiredJwt(ex);

            if (jwtTokenService.getRefreshToken(username) != null) {

                UserRoleEnum role = userService.getUserInfo(username).getRole();
                String newAccessToken = jwtTokenService.generateNewToken(username, role);

                String encodedToken = URLEncoder.encode(newAccessToken, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

                Cookie cookie = new Cookie(COOKIE_AUTH_HEADER, encodedToken);
                cookie.setPath("/");
                response.addCookie(cookie);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(createAuthentication(username));
                SecurityContextHolder.setContext(context);

                filterChain.doFilter(request, response);
            } else {
                throw new IllegalStateException("리프레시 토큰이 존재하지 않습니다.");
            }
        }
    }

    // Authentication 객체 생성 (UPAT 생성)
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String findAccessToken(Cookie[] cookies){
        if(cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) return cookie.getValue();
        }
        return null;
    }
}
