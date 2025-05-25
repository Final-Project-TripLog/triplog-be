package com.ssafy.triplog.elasticsearch.document;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanPostDocument {
    private Long id;
    private String title;
    private String description;
    private String userNickname;
    private String tags;
    private String attractionTitles;
    private String address;
    private LocalDateTime createdAt;
}
