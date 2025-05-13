package com.ssafy.triplog.user.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLoginRequest {
    //    @NotBlank
    private String authorizationCode;

    //    @NotBlank
    private String redirectUri;
}
