package com.ssafy.triplog.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserFindPasswordRequest {
    private String email;                // 사용자 이메일
    private String name;                // 진짜 이름
    private String phone;               // 전화번호 (010-0000-0000 형식)
}
