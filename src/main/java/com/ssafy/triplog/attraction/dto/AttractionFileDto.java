package com.ssafy.triplog.attraction.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttractionFileDto {
    @Schema(type = "string", format = "binary")
    private MultipartFile multipartFile; // 이미지 파일
    private Integer reviewImageOrder;    // 정렬 순서
}
