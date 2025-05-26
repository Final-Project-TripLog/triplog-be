package com.ssafy.triplog.elasticsearch.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostSearchDto {
    private Long id;
    private String title;
    private String description;
    private String userNickname;
    private String tags;
    private String attractionTitles;
    private String address;
    private LocalDateTime createdAt;
}
