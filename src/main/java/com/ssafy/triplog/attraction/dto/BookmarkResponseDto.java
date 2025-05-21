// src/main/java/com/ssafy/triplog/attraction/dto/BookmarkResponseDto.java - 수정 필요
package com.ssafy.triplog.attraction.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponseDto {
    private Long bookmarkTypeNo;
    private Long attractionNo;
    private Integer order;
    // 실제 INSERT 쿼리에서는 이 필드들만 사용됩니다.

    // 응답용 추가 필드 (XML INSERT에서 사용하지 않음)
    private String name;
    private Integer attractionCount;
    private List<AttractionResponseDto> attractions;
}