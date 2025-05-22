package com.ssafy.triplog.myplan.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPlanRequest {
    // MyPlanDto
    private Long no;               // 개인 여행 계획 pk
    private String title;          // 제목
    private String description;    // 계획 설명
    private Long userNo;           // 사용자 pk (참조)

    private List<MyDailyPlanDto> dailyPlans;

}
