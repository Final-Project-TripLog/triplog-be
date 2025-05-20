package com.ssafy.triplog.security.dto;

import com.ssafy.triplog.user.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Spring Security에서 사용자 정보를 나타내는 인터페이스 구현
public class CustomUserDetails implements UserDetails {

    private final UserDto userDto; // 실제 사용자 정보를 담고 있는 DTO

    public CustomUserDetails(UserDto userDto) {
        this.userDto = userDto;
    }

    // 사용자의 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // DB에 저장된 권한 정보(role)를 GrantedAuthority 객체로 변환
        authorities.add(new SimpleGrantedAuthority(userDto.getRole()));
        return authorities;
    }

    // 사용자의 비밀번호 반환
    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    // 사용자의 식별자(이메일) 반환
    @Override
    public String getUsername() {
        return userDto.getEmail();
    }

    // 계정 만료 여부: true = 만료되지 않음
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부: true = 잠기지 않음
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명(비밀번호) 만료 여부: true = 만료되지 않음
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부: true = 활성화됨
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 추가 메소드: 사용자 번호 반환
    public Long getUserNo() {
        return userDto.getNo();
    }

    // 추가 메소드: 사용자 닉네임 반환
    public String getNickname() {
        return userDto.getNickname();
    }
}