package com.ssafy.triplog.planpost.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanAttractionDetailDto {
    private Long no;                       // 관광지별 여행 계획 게시글 pk

    // ⭐ 필드명 수정: visitDate -> visitDate (DB 컬럼명과 일치시키기 위해)
    private LocalDate visitDate;          // 일정 진행 날짜 (visit_date - DB 오타 수정 권장)
    private LocalTime startTime;          // 일정 시작 시간
    private LocalTime endTime;            // 일정 종료 시간
    private Integer moveTime;             // 이동 시간 (분 단위)

    private String attractionTitle;       // 관광지 제목
    private String attractionThumbnail;   // 관광지 썸네일
    private Double attractionRating;      // 관광지 평점
    private Integer writerRating;         // 게시글 작성자 평점

    private Long planPostNo;              // 여행 계획 게시글 pk
    private Long attractionNo;            // 관광지 정보 pk
    private Long reviewNo;                // 관광지 리뷰 pk
}