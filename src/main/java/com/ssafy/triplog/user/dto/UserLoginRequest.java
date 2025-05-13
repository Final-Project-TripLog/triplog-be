package com.ssafy.triplog.user.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    private String email;                // 사용자 이메일
    private String password;             // 사용자 비밀번호
}
