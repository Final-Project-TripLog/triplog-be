package com.ssafy.triplog.planpost.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostDetailResponse {
    // ===== 게시글 기본 정보 =====
    private Long no;                    // 여행 계획 게시글 pk
    private String userNickname;       // 작성자 닉네임
    private String title;              // 제목
    private String description;        // 게시글 소개 글 (nullable)
    private String thumbnail;          // post 썸네일

    private LocalDateTime createdAt;   // 최초 작성 일시
    private LocalDateTime updatedAt;   // 최종 수정 일시

    private Long userNo;               // 작성자 pk
    private Integer forkCount;         // fork 수
    private Integer likedCount;        // 좋아요수
    private Integer viewCount;         // 조회수

    private LocalDateTime startDay;    // 여행 시작일
    private LocalDateTime endDay;      // 여행 종료일
    private Long totalMember;          // 총 인원수

    // ===== 상세 정보 =====
    private List<PlanPostTagDto> tags;                    // 게시글 태그 목록
    private List<PlanAttractionDetailDto> attractionDetails; // 관광지별 세부 계획 목록

    // ===== 사용자 상호작용 정보 (선택사항) =====
    private Boolean isLikedByCurrentUser;  // 현재 사용자의 좋아요 여부
    private Boolean isOwnedByCurrentUser;  // 현재 사용자의 소유 여부
}