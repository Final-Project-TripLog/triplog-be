// src/main/java/com/ssafy/triplog/security/util/AuthenticationUtil.java
package com.ssafy.triplog.security.util;

import com.ssafy.triplog.security.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰에서 현재 로그인한 사용자 정보를 추출하는 유틸리티 클래스
 */
@Component
public class AuthenticationUtil {

    /**
     * JWT 토큰에서 현재 로그인한 사용자 번호 추출
     * @return 사용자 번호 (userNo)
     * @throws RuntimeException 인증되지 않은 사용자인 경우
     */
    public Long getCurrentUserNo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserNo();
    }

    /**
     * JWT 토큰에서 현재 로그인한 사용자의 CustomUserDetails 추출
     * @return CustomUserDetails 객체
     * @throws RuntimeException 인증되지 않은 사용자인 경우
     */
    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    /**
     * JWT 토큰에서 현재 로그인한 사용자의 이메일 추출
     * @return 사용자 이메일
     * @throws RuntimeException 인증되지 않은 사용자인 경우
     */
    public String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails.getUsername(); // 이메일이 username으로 저장됨
    }

    /**
     * JWT 토큰에서 현재 로그인한 사용자의 닉네임 추출
     * @return 사용자 닉네임
     * @throws RuntimeException 인증되지 않은 사용자인 경우
     */
    public String getCurrentUserNickname() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails.getNickname();
    }

    /**
     * 현재 로그인한 사용자가 특정 사용자와 같은지 확인
     * @param targetUserNo 비교할 사용자 번호
     * @return 같으면 true, 다르면 false
     */
    public boolean isCurrentUser(Long targetUserNo) {
        try {
            Long currentUserNo = getCurrentUserNo();
            return currentUserNo.equals(targetUserNo);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 현재 로그인한 사용자가 관리자인지 확인
     * @return 관리자면 true, 아니면 false
     */
    public boolean isCurrentUserAdmin() {
        try {
            CustomUserDetails userDetails = getCurrentUserDetails();
            return userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        } catch (RuntimeException e) {
            return false;
        }
    }
}