package com.ssafy.triplog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.ssafy.triplog.**.repository.mapper")
public class TriplogApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriplogApplication.class, args);
    }
}