package com.example.chatserver.global.security.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j(topic = "JWT UTIL")
public class JwtUtil {
    private final String AUTHORIZATION_KEY = "auth";
    private final String BEARER_PREFIX = "Bearer ";
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret.key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 시간을 동일하게 맞춘 토큰 생성
     * @param payload tokenPayload
     * @return jwtToken
     */
    public String createToken(TokenPayload payload) {
        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(payload.getSub()) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, payload.getRole()) // 사용자 권한
                        .expiration(payload.getExpiresAt()) // 만료 시간
                        .issuedAt(payload.getIat()) // 발급일
                        .id(payload.getJti()) // JWT ID
                        .signWith(secretKey) // 암호화 Key & 알고리즘
                        .compact();
    }

    /**
     * 토큰의 만료 기간 여부 확인
     * @param token 대상 토큰 값
     * @return 만료 여부
     */
    // 토큰이 만료되었는지 확인하는 메서드
    public boolean isTokenExpired(String token) {
        return this.getClaims(token).getExpiration().before(new Date());
    }

    /**
     * 토큰의 유저 정보 반환
     * @param token 대상 토큰 값
     * @return 유저 정보
     */
    public String getUsernameFromToken(String token) {
        // 만료된 토큰에서 클레임을 파싱하되 서명 검증은 생략
        return this.getClaims(token).getSubject();
    }

    /**
     * 헤더값으로 부터 토큰 값 가져오기
     * @param fromHeader 대상 문자열
     * @return 확인된 토큰값
     */
    public String getAccessToken(String fromHeader) {
        return this.extractToken(fromHeader);
    }

    public String extractToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }

        throw new JwtException("엑세스 토큰이 확인되지 않습니다.");
    }

    /**
     * 토큰 만료일자 파싱
     * @param token 대상 토큰값
     * @return 토큰 만료일자
     */
    public Date getTokenIat(String token) {
        return this.getClaims(token).getIssuedAt();
    }

    /**
     * 토큰 유효여부 검증
     * @param token 대상 토큰값
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        return !getClaims(token).isEmpty();
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 엑세스 토큰 재발급용
    public String getUsernameFromExpiredJwt(ExpiredJwtException exception) {
        return exception.getClaims().getSubject();
    }
}