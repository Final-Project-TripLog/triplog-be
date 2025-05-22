// src/main/java/com/ssafy/triplog/attraction/dto/AttractionPreviewResponseDto.java
package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionPreviewResponseDto {
    private Long no;                        // 관광지 정보 pk
    private String title;                  // 관광지명
    private Double latitude;               // 위도
    private Double longitude;              // 경도
    private String thumbnail;              // 대표 이미지
    private Double ratingSum;              // 평점 합계
    private Integer reviewCount;           // 리뷰 카운트
    private String attractionTypeName;     // 관광지 분류
    private String address;                // 기본 주소
}