package com.ssafy.triplog.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.triplog.security.dto.CustomUserDetails;
import com.ssafy.triplog.user.dto.UserLoginRequest;
import com.ssafy.triplog.user.dto.UserResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/users/login"); // 로그인 URL 설정
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 요청 본문에서 사용자 이름 및 비밀번호 추출
            UserLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            // AuthenticationManager를 통해 인증 시도
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("인증 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // 인증 성공시 호출됨
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 권한 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // JWT 토큰 생성 (10시간 유효)
        String token = jwtUtil.createJwt(userDetails.getUsername(), role, 60 * 60 * 10L * 1000);

        // 응답 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer " + token);

        // 응답 바디에 사용자 정보 추가
        UserResponse userResponse = new UserResponse();
        userResponse.setNo(userDetails.getUserNo());
        userResponse.setNickname(userDetails.getNickname());

        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(userResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // 로그인 실패시 401 응답
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.\"}");
    }
}