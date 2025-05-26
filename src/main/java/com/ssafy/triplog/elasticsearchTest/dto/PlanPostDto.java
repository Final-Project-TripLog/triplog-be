package com.ssafy.triplog.elasticsearchTest.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostDto {
    private Long no;                    // 여행 계획 게시글 pk
    private String userNickname;       // 작성자 닉네임
    private String title;              // 제목
    private String description;        // 게시글 소개 글 (nullable)

    private LocalDateTime createdAt;   // 최초 작성 일시
    private LocalDateTime updatedAt;   // 최종 수정 일시
    private String thumbnail;          // post 썸네일

    private Long userNo;               // 작성자 pk
    private Integer forkCount;         // fork 수
    private Integer likedCount;        // 좋아요수
    private Integer viewCount;         // 조회수

    // ⭐ 추가된 필드들
    private LocalDateTime startDay;    // 여행 시작일 (start_day)
    private LocalDateTime endDay;      // 여행 종료일 (end_day)
    private Long totalMember;          // 총 인원수 (total_member)
}