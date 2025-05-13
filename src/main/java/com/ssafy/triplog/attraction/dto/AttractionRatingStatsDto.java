package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionRatingStatsDto {
    private Long attractionNo;      // 관광지 정보 pk
    private Integer ratingCount;    // 평점 개수
    private Long ratingSum;         // 평점 총합
}
