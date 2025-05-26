package com.ssafy.triplog.planpost.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanCommentUpdateRequestDto {
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "댓글은 1~1000자 이내로 작성해주세요")
    private String content;  // 댓글 내용
}