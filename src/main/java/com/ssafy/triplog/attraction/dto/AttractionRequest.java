package com.ssafy.triplog.attraction.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttractionRequest {
    // 📌 관광지 정보
    private String title;
    private String overview;
    private Integer mapLevel;
    private Double latitude;
    private Double longitude;
    private String tel;
    private String address;
    private String addressDetail;
    private String homepage;
    private String apiId;
    private Integer contentId;
    private String thumbnail;
    private Double rating;

    private Integer gugunNo;
    private Integer sidoNo;
    private Integer attractionTypeNo;

//    private List<MultipartFile> files;
//    private List<Integer> reviewImageOrders;


    // 📎 첨부 이미지 정보 (파일 + 순서 등)
    private List<AttractionFileDto> images;
}
