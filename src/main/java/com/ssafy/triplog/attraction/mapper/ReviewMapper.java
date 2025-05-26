// src/main/java/com/ssafy/triplog/attraction/mapper/ReviewMapper.java
package com.ssafy.triplog.attraction.mapper;

import com.ssafy.triplog.attraction.dto.AttractionReviewResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReviewMapper {
    List<AttractionReviewResponseDto> findReviewsByAttraction(
            @Param("attractionNo") Long attractionNo,
            @Param("size") int size,
            @Param("page") int offset  // 여기서 offset으로 이름을 바꾸거나, 아님 #{}으로 직접 SQL에서 접근하게 할 수도 있습니다
    );
    List<AttractionReviewResponseDto> findReviewsByUser(@Param("userNo") Long userNo,
                                                        @Param("page") int page, @Param("size") int size);
    AttractionReviewResponseDto findReviewById(@Param("reviewNo") Long reviewNo);
    void insertReview(AttractionReviewResponseDto reviewDto);
    void updateReview(AttractionReviewResponseDto reviewDto);
    void deleteReview(@Param("reviewNo") Long reviewNo);
}