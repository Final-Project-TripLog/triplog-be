package com.ssafy.triplog.attraction.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionReviewResponseDto {
    private Long no;
    private String userNickname;
    private Integer rating;  // DB의 타입에 맞춤 (INT)
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long attractionNo;
    private Long userNo;

    // 스키마에 없는 필드는 제외
    // private String profileImage; // 스키마에 없음
}