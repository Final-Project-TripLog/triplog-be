package com.ssafy.triplog.user.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long no;                      // 사용자 pk
    private String nickname;            // 닉네임
    private String profileUrl;          // 프로필 사진
}
