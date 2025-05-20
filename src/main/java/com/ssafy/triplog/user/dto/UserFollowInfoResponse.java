package com.ssafy.triplog.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowInfoResponse {
    private Long no;                      // 사용자 pk
    private String nickname;            // 닉네임
    private String profileUrl;          // 프로필 사진
    // 생성자, getter, setter, builder 등은 필요에 따라 추가
}
