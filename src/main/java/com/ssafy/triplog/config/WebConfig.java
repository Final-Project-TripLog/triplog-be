package com.ssafy.triplog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로필 이미지 정적 파일 서빙
        String uploadPath = "file:" + System.getProperty("user.home") + "/triplog/uploads/";

        // 여러 경로 패턴으로 접근 가능하도록 설정
        registry.addResourceHandler("/triplog/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // 1시간 캐싱

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // 1시간 캐싱

        System.out.println("정적 파일 경로 설정: " + uploadPath);
    }
}