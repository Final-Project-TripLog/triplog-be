package com.ssafy.triplog.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Spring의 설정 클래스
public class CorsMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**") // 모든 경로에 적용
                .allowedOriginPatterns("*") // '*' 대신 패턴 사용하여 모든 출처 허용
                // 또는 명시적인 출처 나열: .allowedOrigins("http://localhost:8080")
                .allowedMethods("*") // 모든 HTTP 메서드 허용
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키 포함 허용
                .maxAge(3600); // 프리플라이트 요청 캐시 시간 (초)
    }
}
