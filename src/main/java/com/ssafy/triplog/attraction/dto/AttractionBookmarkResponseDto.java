package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionBookmarkResponseDto {
    private Long no;
    private String name;
    private Integer attractionCount;
    private Long userNo;
}