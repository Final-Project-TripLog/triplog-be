package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDto {
    private Long bookmarkTypeNo;     // 북마크 타입 pk
    private Long attractionNo;       // 관광지 정보 pk
    private Integer order;           // 북마크한 관광지 순서
}
