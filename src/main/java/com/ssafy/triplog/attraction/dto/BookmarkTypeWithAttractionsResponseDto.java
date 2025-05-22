package com.ssafy.triplog.attraction.dto;


import lombok.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkTypeWithAttractionsResponseDto {
    private BookmarkTypeResponseDto bookmarkType;
    private List<AttractionResponseDto> attractions;
}