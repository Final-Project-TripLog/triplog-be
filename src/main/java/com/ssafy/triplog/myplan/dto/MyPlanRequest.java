// 수정된 MyPlanRequest.java
package com.ssafy.triplog.myplan.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPlanRequest {
    // 기본 여행 계획 정보
    private String title;              // 제목
    private String description;        // 계획 설명
    private LocalDateTime startTime;   // ✅ 추가: 여행 시작 시간
    private LocalDateTime endTime;     // ✅ 추가: 여행 종료 시간
    private Long totalMember;          // ✅ 추가: 총 인원

    private List<MyDailyPlanDto> dailyPlans;
}