package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionImageDto {
    private Long no;                       // 이미지 pk
    private String imageUrl;              // 이미지 URL
    private Integer reviewImageOrder;     // 리뷰 이미지 순서 (nullable)

    private Long attractionNo;            // 관광지 정보 pk (nullable)
    private Long attractionReviewNo;      // 관광지 리뷰 pk (nullable)
}
