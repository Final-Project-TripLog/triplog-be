package com.ssafy.triplog.elasticsearchTest.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostTagDto {
    private Long no;              // 태그 기본키
    private Long planPostNo;      // 여행 계획 게시글 pk
    private String name;          // 태그 이름
}
