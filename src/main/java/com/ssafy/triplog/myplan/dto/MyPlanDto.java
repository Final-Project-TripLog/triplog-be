package com.ssafy.triplog.myplan.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyPlanDto {
    private Long no;               // 개인 여행 계획 pk
    private String title;          // 제목
    private String description;    // 계획 설명
    private Long userNo;           // 사용자 pk (참조)
}
