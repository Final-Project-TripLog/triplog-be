package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDetailResponseDto {
    private Long no;                     // 관광지 정보 pk
    private String title;                // 관광지명
    private String overview;             // 관광지 설명
    private Integer mapLevel;            // 지도 확대 정도
    private Double latitude;             // 위도
    private Double longitude;            // 경도
    private String tel;                  // 연락처
    private String address;              // 기본 주소
    private String addressDetail;        // 상세 주소
    private String homepage;             // 홈페이지 주소
    private String apiId;                // 외부 API 연동 ID
    private Long contentId;              // API 고유 콘텐츠 ID
    private String thumbnail;            // 대표 이미지
    private Double ratingSum;            // 평점 합계
    private Integer reviewCount;         // 리뷰 개수
    private String attractionType;       // 관광지 분류명 (프론트에 표시용)
    // rating 필드는 제거 - 프론트엔드에서 계산
}