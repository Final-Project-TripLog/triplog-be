package com.ssafy.triplog.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.triplog.security.dto.CustomUserDetails;
import com.ssafy.triplog.user.dto.UserLoginRequest;
import com.ssafy.triplog.user.dto.UserResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;

// 로그인 요청을 처리하는 필터
@Slf4j // 로깅을 위한 Lombok 어노테이션 추가
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager; // 사용자 인증을 처리하는 매니저
    private final JWTUtil jwtUtil; // JWT 유틸리티
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 처리를 위한 매퍼

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/users/login"); // 로그인 URL 설정 (이 URL로 들어오는 POST 요청만 처리)
    }

    // 로그인 시도 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 요청 본문에서 사용자 이름 및 비밀번호 추출
            UserLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            log.info("로그인 시도: 이메일 = {}", email);

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            // AuthenticationManager를 통해 인증 시도 (UserDetailsService.loadUserByUsername 호출됨)
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            log.error("로그인 처리 중 오류 발생", e);
            throw new RuntimeException("인증 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 인증 성공 시 호출되는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // 인증된 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 권한 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        Long userNo = userDetails.getUserNo(); // 사용자 ID 가져오기
        // 3. jwt에서 사용자 NO 가져오기

        // JWT 토큰 생성 (10시간 유효)
        String token = jwtUtil.createJwt(userDetails.getUsername(), role,userNo, 60 * 60 * 10L * 1000);
        // 4. 사용자 No를 추가해서 JWT 토큰 생성하기, userNo 넘겨주기

        // 토큰 내용 디버깅
        printTokenInfo(token);

        // 응답 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer " + token);

        // 응답 바디에 사용자 정보 추가
        UserResponse userResponse = new UserResponse();
        userResponse.setNo(userDetails.getUserNo());
        userResponse.setNickname(userDetails.getNickname());

        // JSON 형식으로 응답 반환
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(userResponse));
    }
    /**
     * JWT 토큰 정보를 콘솔에 출력하는 메서드
     * @param token JWT 토큰
     */
    private void printTokenInfo(String token) {
        try {
            // 토큰 전체 출력
            log.info("생성된 JWT 토큰: {}", token);

            // 토큰 파싱
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                log.warn("유효하지 않은 JWT 형식입니다.");
                return;
            }

            // Base64 디코딩
            Base64.Decoder decoder = Base64.getUrlDecoder();

            // 헤더 디코딩
            String header = new String(decoder.decode(chunks[0]));
            log.info("JWT 헤더: {}", header);

            // 페이로드(클레임) 디코딩
            String payload = new String(decoder.decode(chunks[1]));
            log.info("JWT 페이로드: {}", payload);

            // 토큰에서 추출한 정보 확인
            log.info("토큰에서 추출한 이메일: {}", jwtUtil.getUsername(token));
            log.info("토큰에서 추출한 역할: {}", jwtUtil.getRole(token));
            log.info("토큰에서 추출한 PK: {}", jwtUtil.getUserNo(token));

            // userNo 추출 메서드가 구현되었다면 추가
            // log.info("토큰에서 추출한 사용자 ID: {}", jwtUtil.getUserNo(token));

            log.info("토큰 만료 여부: {}", jwtUtil.isExpired(token) ? "만료됨" : "유효함");
        } catch (Exception e) {
            log.error("토큰 정보 출력 중 오류 발생", e);
        }
    }
    // 인증 실패 시 호출되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        // 로그인 실패 시 호출됨
        log.warn("로그인 실패: {}", failed.getMessage());

        // 로그인 실패시 401 응답 (Unauthorized)
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.\"}");
    }
}