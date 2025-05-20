package com.ssafy.triplog.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowInfoDto {
    private Long no;                      // 팔로우 정보 pk
    private Long follow;                // 팔로우
    private Long follower;             // 팔로워
    private LocalDateTime createAt;           // 팔로우 시간
}
