package com.ssafy.triplog.attraction.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDocument {
    private Long id;
    private String title;
    private String region;
    private String description;
}
