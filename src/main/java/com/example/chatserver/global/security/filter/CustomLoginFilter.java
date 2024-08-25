package com.example.chatserver.global.security.filter;

import com.example.chatserver.domain.user.dto.UserDTO;
import com.example.chatserver.domain.user.entity.UserDetailsImpl;
import com.example.chatserver.domain.user.entity.UserRoleEnum;
import com.example.chatserver.global.security.service.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.example.chatserver.global.constant.Constants.COOKIE_AUTH_HEADER;

@Slf4j(topic = "CustomLoginFilter")
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtTokenService jwtTokenService;

    public CustomLoginFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
        setFilterProcessesUrl("/api/users/login"); // 로그인 처리 경로 설정(매우매우 중요)
        super.setUsernameParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("로그인 단계 진입");
        try {
            UserDTO.Login requestDto = new ObjectMapper().readValue(request.getInputStream(), UserDTO.Login.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){
        log.info("로그인 성공 및 JWT 생성");

        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = URLEncoder.encode(jwtTokenService.generateNewToken(username, role), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        Cookie cookie = new Cookie(COOKIE_AUTH_HEADER, token);
        cookie.setPath("/");
        response.addCookie(cookie);

        log.info("쿠키: {}", cookie.getValue());

        sendResponseMsg(response, HttpStatus.OK.value(),"로그인 성공 및 토큰 발급");
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){
        log.error("로그인 실패");

        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디와 비밀번호를 확인해주세요.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다.";
        } else {
            errorMessage = "알 수없는 오류입니다.";
        }
        log.error("로그인 실패 :" + errorMessage);
        sendResponseMsg(response, HttpStatus.UNAUTHORIZED.value(),errorMessage);
    }

    private void sendResponseMsg(HttpServletResponse response, int statusCode, String msg){
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        try {

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", statusCode);
            responseBody.put("data", msg);

            PrintWriter writer = response.getWriter();
            writer.print(new ObjectMapper().writeValueAsString(responseBody));
            writer.flush();
            writer.close();
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }
}
