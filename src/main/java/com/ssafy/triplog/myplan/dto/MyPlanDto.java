package com.ssafy.triplog.myplan.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPlanDto {
    private Long no;               // 개인 여행 계획 pk
    private String title;          // 제목
    private String description;    // 계획 설명
    private Long userNo;           // 사용자 pk (참조)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime updateTime;
    private Long totalMember;
}
