package com.example.chatserver.global.message;

import com.example.chatserver.global.security.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import static com.example.chatserver.global.constant.Constants.COOKIE_AUTH_HEADER;

@Slf4j(topic = "WebSocketInterceptor")
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("인터셉터 접근 확인용");

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info("메세지 헤더 확인 ㅠㅠ: {}", String.valueOf(accessor.getMessageHeaders()));

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            this.setAuthenticate(accessor);
        }
        return message;
    }

    private void setAuthenticate(final StompHeaderAccessor accessor) {
        String firstValue = accessor.getFirstNativeHeader(COOKIE_AUTH_HEADER);
        log.info("초기 엑세스 토큰 값: {}", firstValue);
        String accessTokenValue = jwtTokenService.getAccessToken(firstValue);
        log.info("바로 가지고 온 엑세스 토큰 값: {}", accessTokenValue);

//        String prefix = "Bearer ";
//        String accessToken = accessTokenValue.substring(prefix.length());
//
//        log.info("인터셉터 인증 객체 엑세스 토큰 확인: {}", accessToken);

        // 좀 더 실용적인 인증 수단 마련 필요
        String email;
        try {
            jwtTokenService.validAccessToken(accessTokenValue);
            email = jwtTokenService.getUsernameFromAccessToken(accessTokenValue);
        } catch (ExpiredJwtException ex) {
            email = jwtTokenService.getUsernameFromExpiredJwt(ex);
        }

        log.info("소켓 CONNECT 시도, 유저 이메일 : {}", email);

        Authentication authentication = this.createAuthentication(email);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);
    }

    private Authentication createAuthentication(final String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}