package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionImageResponseDto {
    private Long no;
    private String imageUrl;
    private Integer reviewImageOrder;
    private Long attractionNo;
    private Long attractionReviewNo;
}