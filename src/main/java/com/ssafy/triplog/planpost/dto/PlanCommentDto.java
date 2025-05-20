package com.ssafy.triplog.planpost.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanCommentDto {
    private Long no;                    // 여행 계획 게시글 댓글 pk
    private String content;             // 댓글 내용
    private String userNickname;        // 댓글 작성자 닉네임
    private LocalDateTime createdAt;    // 최초 작성 일시
    private LocalDateTime updatedAt;    // 최종 수정 일시
    private Integer level;              // 계층 레벨 (최대 10)
    private String path;                // 계층 path (예: 00000-00000)
    private Integer childCount;         // 자식 댓글 수 (최대 9999개)

    private Long planPostNo;            // 여행 계획 게시글 pk
    private Long userNo;                // 댓글 작성자 pk
    private Long parentNo;              // 상위 여행 계획 게시글 댓글 pk (nullable)

}
