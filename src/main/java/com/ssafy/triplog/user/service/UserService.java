package com.ssafy.triplog.user.service;

import com.ssafy.triplog.user.dto.UserResponse;
import com.ssafy.triplog.user.dto.UserServiceDto;
import com.ssafy.triplog.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(UserServiceDto userServiceDto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(userServiceDto.getEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화
        userServiceDto.setPassword(passwordEncoder.encode(userServiceDto.getPassword()));

        // 기본 권한 설정
        userServiceDto.setRole("ROLE_USER");

        // 소셜 타입 설정
        if (userServiceDto.getSocialType() == null) {
            userServiceDto.setSocialType("LOCAL");
        }

        // 사용자 저장
        Long userNo = userRepository.save(userServiceDto);

        // 응답 생성
        UserResponse response = new UserResponse();
        response.setNo(userNo);
        response.setNickname(userServiceDto.getNickname());
        response.setProfileUrl(userServiceDto.getProfileUrl());

        return response;
    }
}