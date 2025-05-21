// src/main/java/com/ssafy/triplog/attraction/mapper/ReviewMapper.java
package com.ssafy.triplog.attraction.mapper;

import com.ssafy.triplog.attraction.dto.AttractionReviewResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReviewMapper {
    List<AttractionReviewResponseDto> findReviewsByAttraction(@Param("attractionNo") Long attractionNo,
                                                              @Param("page") int page, @Param("size") int size);
    List<AttractionReviewResponseDto> findReviewsByUser(@Param("userNo") Long userNo,
                                                        @Param("page") int page, @Param("size") int size);
    AttractionReviewResponseDto findReviewById(@Param("reviewNo") Long reviewNo);
    void insertReview(AttractionReviewResponseDto reviewDto);
    void updateReview(AttractionReviewResponseDto reviewDto);
    void deleteReview(@Param("reviewNo") Long reviewNo);
}