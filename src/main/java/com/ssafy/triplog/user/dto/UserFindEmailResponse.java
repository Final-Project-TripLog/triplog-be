package com.ssafy.triplog.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserFindEmailResponse {
    private Long no;                      // 사용자 pk
    private String email;                // 사용자 이메일
}
