package com.ssafy.triplog.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // Spring 컴포넌트로 등록
public class JWTUtil {

    private SecretKey secretKey; // JWT 서명에 사용될 비밀 키

    // 생성자에서 application.properties에서 jwt.secret 값을 주입받아 비밀 키 생성
    public JWTUtil(@Value("${jwt.secret}")String secret) {
        // 주어진 비밀 값을 사용하여 HS256 알고리즘용 비밀 키 생성
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰에서 사용자 이름(이메일) 추출
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 토큰에서 역할(권한) 추출
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // 토큰 만료 여부 확인
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    // 토큰에서 사용자 ID 추출하는 메서드 추가
    // 2. 토큰에서 사용자 ID 추출하기 함수 구현하기
    public Long getUserNo(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .get("userNo", Long.class);
    }
    // JWT 토큰 생성
    public String createJwt(String username, String role,Long userNo, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username) // 사용자 이름(이메일) 저장
                .claim("role", role) // 역할(권한) 저장
                .claim("userNo", userNo) // 사용자 PK 추가
                // 1. UserNo 추가해서 JWT 발급 받기 함수 구현하기
                .issuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey) // 비밀 키로 서명
                .compact(); // 토큰 생성
    }
}