package com.example.chatserver.global.security.service;

import com.example.chatserver.domain.user.dto.UserDTO;
import com.example.chatserver.domain.user.entity.UserRoleEnum;
import com.example.chatserver.domain.user.service.UserService;
import com.example.chatserver.global.security.util.JwtUtil;
import com.example.chatserver.global.security.util.TokenPayload;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.chatserver.global.constant.Constants.REDIS_REFRESH_KEY;

@Service
@Slf4j(topic = "Jwt Service")
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final long ACCESS_TOKEN_EAT = 30 * 1000L; // 1H
    private final long REFRESH_TOKEN_EAT = 7 * 24 * 60 * 60 * 1000L; // 7D

    /**
     * 로그인 인증이 끝난 후 신규 토큰 발행
     * @param email 요청한 회원 이메일
     * @return 새로 발급한 토큰 정보
     */
    public String generateNewToken(String email, UserRoleEnum role){

        Date date = new Date();

        String tokenValue = jwtUtil.createToken(createTokenPayload(email, date, REFRESH_TOKEN_EAT, role)).substring(7);
        //리프레시 토큰 발행
        redisTemplate.opsForValue().set(REDIS_REFRESH_KEY + email, tokenValue, REFRESH_TOKEN_EAT, TimeUnit.MILLISECONDS);

        return jwtUtil.createToken(createTokenPayload(email, date, ACCESS_TOKEN_EAT, role));
    }

    /**
     * 입력 받은 엑세스 토큰 확인
     * @param token 대상 토큰 값
     * @return 기존 또는 갱신된 토큰
     */
    public String validAccessToken(String token){

        String accessToken = extractValue(token);
        String email = jwtUtil.getUsernameFromToken(accessToken);

        if(!userService.existUserEmail(email))
            throw new JwtException("유효하지 않은 엑세스 토큰입니다.");

        return token;
    }

    // 엑세스 토큰 재발급용 메소드
    public String getUsernameFromExpiredJwt(ExpiredJwtException exception) {
        return jwtUtil.getUsernameFromExpiredJwt(exception);
    }

    /**
     * 토큰의 유저 정보 반환
     * @param token 대상 토큰 값
     * @return 유저 정보
     */
    public UserDTO getUserFromToken(String token) {
        return userService.getUserInfo(jwtUtil.getUsernameFromToken(token));
    }
    /**
     * 토큰 값 디코딩 및 추출
     * @param token 토큰이 있는 데이터
     * @return 추출한 토큰 값
     */
    public String extractValue(String token){
        return jwtUtil.extractToken(URLDecoder.decode(token, StandardCharsets.UTF_8));
    }

    private TokenPayload createTokenPayload(String email, Date date, long seconds, UserRoleEnum role){
        return  new TokenPayload(email, UUID.randomUUID().toString(),date,new Date(date.getTime()+seconds),role);
    }

    // 리프레쉬 토큰 반환 메소드
    public String getRefreshToken(String email) {
        String refreshToken  = redisTemplate.opsForValue().get(REDIS_REFRESH_KEY+email);

        // 유효성 검증 로직 추후 추가
        return refreshToken;
    }
}
