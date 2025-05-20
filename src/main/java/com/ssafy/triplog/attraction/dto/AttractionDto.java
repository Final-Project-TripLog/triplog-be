package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDto {
    private Long no;                        // 관광지 정보 pk
    private String title;                  // 관광지명
    private String overview;               // 관광지 설명
    private Integer mapLevel;              // 지도 확대 정도
    private Double latitude;               // 위도
    private Double longitude;              // 경도
    private String tel;                    // 연락처
    private String address;                // 기본 주소
    private String addressDetail;          // 상세 주소
    private String homepage;               // 홈페이지 주소
    private String apiId;                  // 외부 API 연동 ID
    private Integer contentId;             // API 고유 콘텐츠 ID
    private String thumbnail;              // 대표 이미지
    private Double rating;                 // 평점

    private Integer gugunNo;               // 구군 코드
    private Integer sidoNo;                // 시도 코드
    private Integer attractionTypeNo;      // 관광지 분류

}
