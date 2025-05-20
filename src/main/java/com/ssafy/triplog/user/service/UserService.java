// src/main/java/com/ssafy/triplog/user/service/UserService.java
package com.ssafy.triplog.user.service;

import com.ssafy.triplog.user.dto.*;
import java.util.List;

public interface UserService {
    // 회원가입
    UserResponse registerUser(UserServiceDto userServiceDto);

    // 회원 상세 정보 조회
    UserServiceDto getUserDetail(Long userNo);

    // 회원탈퇴
    boolean withdrawUser(Long userNo, String password);

    // 회원정보 수정
    UserResponse updateUser(Long userNo, UserDto userDto);

    // 모든 회원 조회 (관리자)
    List<UserServiceDto> getAllUsers();

    // 이메일 찾기
    UserFindEmailResponse findUserEmail(UserFindEmailRequest request);

    // 비밀번호 찾기
    String findUserPassword(UserFindPasswordRequest request);

    // 팔로워 목록 조회
    List<UserFollowInfoResponse> getUserFollowers(Long userNo);

    // 팔로잉 목록 조회
    List<UserFollowInfoResponse> getUserFollowing(Long userNo);

    // 팔로우
    boolean followUser(Long followerId, Long followingId);

    // 언팔로우
    boolean unfollowUser(Long followerId, Long followingId);

    // 소셜 로그인 처리
    UserResponse handleSocialLogin(String provider, UserSocialLoginRequest request);
}