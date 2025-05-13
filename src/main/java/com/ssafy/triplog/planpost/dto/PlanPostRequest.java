package com.ssafy.triplog.planpost.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostRequest {
    private Long no;                    // 여행 계획 게시글 pk
    private String userNickname;       // 작성자 닉네임
    private String title;              // 제목
    private String description;        // 게시글 소개 글 (nullable)

    private Long userNo;               // 작성자 pk
    private Integer forkCount;          //fork 수
    private Integer likedCount;          //좋아요수
    private Integer viewCount;          //조회수
    
    private List<PlanAttractionDetailDto> attractions;
}
