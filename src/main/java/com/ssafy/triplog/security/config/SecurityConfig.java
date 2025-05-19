package com.ssafy.triplog.security.config;

import com.ssafy.triplog.security.jwt.JWTFilter;
import com.ssafy.triplog.security.jwt.JWTUtil;
import com.ssafy.triplog.security.jwt.LoginFilter;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 폼 로그인 비활성화
        http.formLogin(formLogin -> formLogin.disable());

        // HTTP Basic 인증 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());

        // 경로별 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // Swagger 관련 경로
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
                        AntPathRequestMatcher.antMatcher("/**")
                ).permitAll()
                // 관리자 기능
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/**/api/admin/**")
                ).hasRole("ADMIN")
                // 인증된 사용자만 접근 가능한 경로들
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/**/api/users/{userNo}/**"),
                        AntPathRequestMatcher.antMatcher("/**/api/attraction/**"),
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

        // 로그인 필터 추가
        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        // JWT 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 세션 관리 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*")); // 패턴으로 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // JWT를 위한 헤더 노출
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}