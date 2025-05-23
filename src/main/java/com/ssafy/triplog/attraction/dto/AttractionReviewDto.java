package com.ssafy.triplog.attraction.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionReviewDto {
    private Long no;                   // 관광지 리뷰 pk
    private String userNickname;      // 작성자 닉네임
    private Integer rating;           // 평점 (0~5 등)
    private String content;           // 리뷰 내용 (nullable)

    private LocalDateTime createAt;   // 최초 작성 일시
    private LocalDateTime updateAt;   // 최종 수정 일시

    private Long attractionNo;        // 관광지 ID
    private Long userNo;              // 작성자 ID
    private String profileUrl;
}
