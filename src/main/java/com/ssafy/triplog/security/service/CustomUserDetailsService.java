package com.ssafy.triplog.security.service;

import com.ssafy.triplog.security.dto.CustomUserDetails;
import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Spring의 서비스 컴포넌트로 등록
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // 사용자 정보 조회를 위한 리포지토리

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 이름(이메일)으로 사용자 정보를 로드하는 메소드 - Spring Security에서 인증 시 사용됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 이메일로 사용자 정보 조회
        UserDto userDto = userRepository.findByEmail(username);

        if (userDto != null) {
            // 사용자 정보가 있으면 CustomUserDetails 객체로 변환하여 반환
            return new CustomUserDetails(userDto);
        }

        // 사용자 정보가 없으면 예외 발생
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
}