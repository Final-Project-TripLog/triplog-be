// src/main/java/com/ssafy/triplog/attraction/mapper/AttractionMapper.java
package com.ssafy.triplog.attraction.mapper;

import com.ssafy.triplog.attraction.dto.AttractionImageResponseDto;
import com.ssafy.triplog.attraction.dto.AttractionResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AttractionMapper {
    // AttractionMapper.java
    List<AttractionResponseDto> findAttractions(
            @Param("types") List<String> types,
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("size") int size,
            @Param("page") int offset // 여기서 파라미터 이름을 offset으로 변경하거나, 아니면 XML에서 직접 #{page}로 접근
    );
    AttractionResponseDto findById(@Param("attractionNo") Long attractionNo);
    List<AttractionImageResponseDto> findImagesByAttractionId(@Param("attractionNo") Long attractionNo);
    List<AttractionResponseDto> findAttractionsByBookmarkType(@Param("bookmarkTypeNo") Long bookmarkTypeNo,
                                                              @Param("page") int page, @Param("size") int size);
    void increaseRatingSum(@Param("attractionNo") Long attractionNo, @Param("rating") Integer rating);
    void decreaseRatingSum(@Param("attractionNo") Long attractionNo, @Param("rating") Integer rating);
    void increaseReviewCount(@Param("attractionNo") Long attractionNo);
    void decreaseReviewCount(@Param("attractionNo") Long attractionNo);
}