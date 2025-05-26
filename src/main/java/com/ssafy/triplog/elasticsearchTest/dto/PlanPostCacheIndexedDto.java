package com.ssafy.triplog.elasticsearchTest.dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostCacheIndexedDto {
    private Long no;                    // 게시글 PK
    private String userNickname;       // 작성자 닉네임
    private String title;              // 제목
    private String description;        // 게시글 소개글
    private LocalDateTime createdAt;   // 작성일시
    private LocalDateTime updatedAt;   // 수정일시
    private Long userNo;               // 작성자 PK
    private Integer forkCount;         // fork 수
    private Integer viewCount;         // 조회수
    private Integer likedCount;        // 좋아요수
    private String thumbnail;          // 썸네일
    private LocalDateTime startDay;    // 여행 시작일
    private LocalDateTime endDay;      // 여행 종료일
    private Integer totalMember;       // 총 인원수

    // 🔥 역정규화된 필드들
    private String tags;               // "서울,맛집,여행"
    private String attractionTitles;   // "경복궁,남산타워,한강공원"
    private String addresses;          // "서울시 종로구,서울시 중구"
    private String searchAll;          // 🚀 통합 검색 컬럼
}