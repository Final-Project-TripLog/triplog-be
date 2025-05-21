package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkTypeResponseDto {
    private Long no;
    private String name;
    private Integer attractionCount;
    private Long userNo;
}