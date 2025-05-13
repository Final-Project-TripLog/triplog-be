package com.ssafy.triplog.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceDto {
    private String email;                // 사용자 이메일
    private String password;             // 사용자 비밀번호
    private String socialType;           // 소셜 타입 (GOOGLE, KAKAO, NAVER, LOCAL)
    private String socialId;             // 소셜 아이디
    private String nickname;            // 닉네임
    private String name;                // 진짜 이름
    private String profileUrl;          // 프로필 사진
    private String phone;               // 전화번호 (010-0000-0000 형식)
    private String address;             // 기본 주소
    private String addressDetail;       // 상세 주소
    private Integer followCount;         // 팔로우 수
    private Integer followerCount;        // 팔로우 수

    // 생성자, getter, setter, builder 등은 필요에 따라 추가
}
