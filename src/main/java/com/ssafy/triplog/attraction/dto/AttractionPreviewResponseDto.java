package com.ssafy.triplog.attraction.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionPreviewResponseDto {
//    private Long no;                        // 관광지 정보 pk
//    private String title;                  // 관광지명
//    private Double latitude;               // 위도
//    private Double longitude;              // 경도
//    private String thumbnail;              // 대표 이미지
//    private Double rating;                 // 평점
//    private Integer attractionTypeNo;      // 관광지 분류
//    private Integer reviewCount;           // 리뷰 카운트
//    private String address;                // 기본 주소
    private Long no;
    private String userNickname;
    private Integer rating;  // DB의 타입에 맞춤 (INT)
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long attractionNo;
    private Long userNo;
    // sidoNo와 gugunNo는 테이블에 없으므로 삭제
}
