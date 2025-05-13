package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkTypeDto {
    private Long no;                   // 북마크 타입 pk
    private String name;              // 북마크명
    private Integer attractionCount;  // 북마크한 관광지 개수
    private Long userNo;              // 사용자 pk
}
