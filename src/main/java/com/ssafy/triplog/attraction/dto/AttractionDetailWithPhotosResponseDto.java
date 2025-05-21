package com.ssafy.triplog.attraction.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDetailWithPhotosResponseDto {
    private AttractionDetailResponseDto attractionDetail;
    private List<AttractionPhotoResponseDto> photos;
}