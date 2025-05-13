package com.ssafy.triplog.planpost.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostResponse {
    private Long no;                    // 여행 계획 게시글 pk
    private String userNickname;       // 작성자 닉네임
    private String title;              // 제목
    private String description;        // 게시글 소개 글 (nullable)
    private String thumbnail;           //post 썸네일
    
    private LocalDateTime createdAt;   // 최초 작성 일시
    private LocalDateTime updatedAt;   // 최종 수정 일시

    private Long userNo;               // 작성자 pk
    private Integer forkCount;          //fork 수
    private Integer likedCount;          //좋아요수
    private Integer viewCount;          //조회수

    private List<PlanPostTagDto> tags;
}
