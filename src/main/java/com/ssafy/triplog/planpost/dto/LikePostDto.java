package com.ssafy.triplog.planpost.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LikePostDto {
    private Long userNo;           // 사용자 pk
    private Long planPostNo;       // 여행 계획 게시글 pk
    private LocalDateTime likedAt; // 좋아요한 시간
}
