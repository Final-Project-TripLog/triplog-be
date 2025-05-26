package com.ssafy.triplog.attraction.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResponseDto {
    private Long no;
    private String title;
    private Double latitude;
    private Double longitude;
    private String thumbnail;
    private Double ratingSum;  // DB 스키마에 있는 값
    private Integer reviewCount;
    private String attractionTypeName;  // DB 스키마에는 attraction_type_name
    private String address;
    private String overview;
}