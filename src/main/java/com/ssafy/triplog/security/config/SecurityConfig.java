package com.ssafy.triplog.security.config;

import com.ssafy.triplog.security.jwt.JWTFilter;
import com.ssafy.triplog.security.jwt.JWTUtil;
import com.ssafy.triplog.security.jwt.LoginFilter;
import com.ssafy.triplog.security.jwt.UserResourceOwnershipFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration // Spring의 설정 클래스임을 명시
@EnableWebSecurity // Spring Security 활성화
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration; // 인증 관리자 설정을 위한 객체
    private final JWTUtil jwtUtil; // JWT 관련 유틸리티

    // 생성자 주입
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    // 인증 관리자 Bean 등록 - 사용자 인증 처리를 담당
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 암호화를 위한 인코더 Bean 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인 구성 - 애플리케이션의 보안 정책을 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화 - REST API에서는 일반적으로 CSRF 보호가 불필요
        http.csrf(csrf -> csrf.disable());

        // 폼 로그인 비활성화 - JWT 기반 인증을 사용하므로
        http.formLogin(formLogin -> formLogin.disable());

        // HTTP Basic 인증 비활성화 - JWT를 사용하므로 필요 없음
        http.httpBasic(httpBasic -> httpBasic.disable());

        // 경로별 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // Swagger 관련 경로는 인증 없이 접근 가능
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/**/swagger-ui/**"),
                        AntPathRequestMatcher.antMatcher("/**/swagger-ui.html"),
                        AntPathRequestMatcher.antMatcher("/**/v3/api-docs/**"),
                        AntPathRequestMatcher.antMatcher("/**/api-docs/**")
                ).permitAll()
                // 인증이 필요 없는 API 경로들
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/**/api/users/signup"),
                        AntPathRequestMatcher.antMatcher("/**/api/users/login"),
                        AntPathRequestMatcher.antMatcher("/**/api/users/find-email"),
                        AntPathRequestMatcher.antMatcher("/**/api/users/find-password"),
                        AntPathRequestMatcher.antMatcher("/**/api/users/check-email"),
                        AntPathRequestMatcher.antMatcher("/**/api/users/check-nickname"),
                        AntPathRequestMatcher.antMatcher("/**/api/attraction/**")
//                        AntPathRequestMatcher.antMatcher("/**") // 테스트를 위해 일시적으로 모든 경로 허용 (실제 운영에서는 제거 필요)
                ).permitAll()
                // 관리자 기능 - ADMIN 역할을 가진 사용자만 접근 가능
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/**/api/admin/**")
                ).hasRole("ADMIN")
                // 인증된 사용자만 접근 가능한 경로들
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/**/api/users/{userNo}/**"),
                        AntPathRequestMatcher.antMatcher("/**/api/review/**"),
                        AntPathRequestMatcher.antMatcher("/**/api/bookmarks/**"),
                        AntPathRequestMatcher.antMatcher("/**/api/myplans/**"),
                        AntPathRequestMatcher.antMatcher("/**/api/planposts/**"),
                        AntPathRequestMatcher.antMatcher("/**/api/plan-comments/**")
                ).authenticated()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated());

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 로그인 필터 추가 - /api/users/login 경로에서 인증 처리
        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        // JWT 필터 추가 - 모든 요청에 대해 JWT 토큰 검증
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 사용자 자원 소유권 필터 추가 - JWT 인증 이후 리소스 접근 권한 검증
        // JWTFilter 다음에 실행되어, 인증된 사용자의 리소스 접근 권한을 확인
        http.addFilterAfter(new UserResourceOwnershipFilter(jwtUtil), JWTFilter.class);


        // 세션 관리 설정 - JWT를 사용하므로 세션은 STATELESS로 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // CORS 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ 여러 출처 허용
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:8080"
//                "http://127.0.0.1:5173",
//                "https://dev.triplog.com",
//                "https://triplog.com"
        ));

        // ✅ 허용할 메서드 지정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ 허용할 헤더 지정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));

        // ✅ 클라이언트에서 Authorization 헤더 확인 가능
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // ✅ 쿠키, 인증 정보 포함 요청 허용
        configuration.setAllowCredentials(true);

        // ✅ 프리플라이트 요청 캐시 1시간
        configuration.setMaxAge(3600L);

        // ✅ 모든 경로에 적용 >> 규칙 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}