package com.ssafy.triplog.user.service;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserResponse;
import com.ssafy.triplog.user.dto.UserServiceDto;
import com.ssafy.triplog.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Spring의 서비스 컴포넌트
public class UserService {

    private final UserRepository userRepository; // 사용자 정보 저장/조회를 위한 리포지토리
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자 등록 (회원가입) 처리
    public UserResponse registerUser(UserServiceDto userServiceDto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(userServiceDto.getEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화 - 평문 비밀번호를 BCrypt로 해시화
        userServiceDto.setPassword(passwordEncoder.encode(userServiceDto.getPassword()));

        // 기본 권한 설정 - 일반 사용자 권한 부여
        userServiceDto.setRole("ROLE_USER");

        // 소셜 타입 설정 - 소셜 로그인이 아닌 경우 "LOCAL"로 설정
        if (userServiceDto.getSocialType() == null) {
            userServiceDto.setSocialType("LOCAL");
        }

        // 사용자 DB에 저장
        Long userNo = userRepository.save(userServiceDto);

        // 응답 객체 생성
        UserResponse response = new UserResponse();
        response.setNo(userNo);
        response.setNickname(userServiceDto.getNickname());
        response.setProfileUrl(userServiceDto.getProfileUrl());

        return response;
    }

    // 사용자 상세 정보 조회
    public UserServiceDto getUserDetail(Long userNo) {
        UserDto userDto = userRepository.findById(userNo);
        if (userDto == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userNo);
        }

        // UserDto를 UserServiceDto로 변환 (필요한 필드만 복사)
        UserServiceDto userServiceDto = new UserServiceDto();
        userServiceDto.setEmail(userDto.getEmail());
        userServiceDto.setNickname(userDto.getNickname());
        userServiceDto.setName(userDto.getName());
        userServiceDto.setProfileUrl(userDto.getProfileUrl());
        userServiceDto.setPhone(userDto.getPhone());
        userServiceDto.setAddress(userDto.getAddress());
        userServiceDto.setAddressDetail(userDto.getAddressDetail());
        // 다른 필드도 필요에 따라 설정...

        return userServiceDto;
    }
}