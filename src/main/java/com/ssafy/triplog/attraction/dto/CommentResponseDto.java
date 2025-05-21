package com.ssafy.triplog.attraction.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long no;
    private String userNickname;
    private String profileImage;
    private String content;
    private LocalDateTime createdAt;
}