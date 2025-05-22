package com.ssafy.triplog.attraction.dto;

import lombok.*;
import java.util.List;

// 추가하거나 이름 통일이 필요한 클래스
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDetailWithImagesResponseDto {
    private AttractionResponseDto attraction;
    private List<AttractionImageResponseDto> images;
}