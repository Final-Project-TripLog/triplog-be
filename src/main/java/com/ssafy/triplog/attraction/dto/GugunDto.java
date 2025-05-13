package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GugunDto {
    private Integer gugunNo;   // 구군 번호 (pk)
    private Integer sidoNo;    // 시도 번호 (foreign key)
    private String name;       // 구군명
}
