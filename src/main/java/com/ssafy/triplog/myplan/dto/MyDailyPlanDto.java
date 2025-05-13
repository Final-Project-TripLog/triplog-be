package com.ssafy.triplog.myplan.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyDailyPlanDto {
    private Long no;                        // 관광지별 개인 여행 계획 pk
    private LocalDate visitedDate;         // 일정 진행 날짜
    private LocalTime startTime;           // 일정 시작 시간
    private LocalTime endTime;             // 일정 종료 시간
    private Long moveTime;                 // 이동 시간 (분 단위)

    private String attractionTitle;        // 관광지 정보 제목
    private String attractionThumbnail;    // 관광지 썸네일
    private Double attractionLatitude;     // 위도
    private Double attractionLongitude;    // 경도
    private Double attractionRating;       // 평점

    private String memo;                   // 메모

    private Long attractionNo;             // 관광지 ID
    private Long myPlanNo;                 // 개인 여행 계획 ID

}
