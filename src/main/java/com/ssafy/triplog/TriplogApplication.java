package com.ssafy.triplog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TriplogApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriplogApplication.class, args);
    }
}