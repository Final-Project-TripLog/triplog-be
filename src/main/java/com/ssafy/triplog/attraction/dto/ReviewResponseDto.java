package com.ssafy.triplog.attraction.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long no;
    private String userNickname;
    private String profileImage;
    private Double rating;
    private String content;
    private LocalDateTime createAt;  // 원본 데이터에 맞춰 createAt으로 명명
}