package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionPreviewResponse {
    private Long no;                        // 관광지 정보 pk
    private String title;                  // 관광지명
    private Double latitude;               // 위도
    private Double longitude;              // 경도
    private String thumbnail;              // 대표 수미지
    private Double rating;                 // 평점
    private Integer attractionTypeNo;      // 관광지 분류
    private Integer reviewCount;            // 리뷰
    private String address;                // 기본 주소

}
