package com.ssafy.triplog.attraction.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponseDto {
    private Long booknarkTypeNo;  // DB 컬럼명과 일치
    private String name;
    private Integer attractionCount;
    private List<AttractionResponseDto> attractions;
}