package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionTypeDto {
    private Integer no;     // 관광지 분류 아이디
    private String name;    // 관광지 분류명
}
