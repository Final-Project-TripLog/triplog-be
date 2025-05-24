package com.ssafy.triplog.planpost.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanCommentRequestDto {
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "댓글은 1~1000자 이내로 작성해주세요")
    private String content;             // 댓글 내용

    @NotNull(message = "게시글 번호는 필수입니다")
    private Long planPostNo;            // 여행 계획 게시글 pk

    private Long parentNo;              // 상위 여행 계획 게시글 댓글 pk (nullable)
}