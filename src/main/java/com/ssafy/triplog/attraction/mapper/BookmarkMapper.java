// src/main/java/com/ssafy/triplog/attraction/mapper/BookmarkMapper.java
package com.ssafy.triplog.attraction.mapper;

import com.ssafy.triplog.attraction.dto.BookmarkResponseDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookmarkMapper {
    List<BookmarkTypeResponseDto> findBookmarkTypesByUser(@Param("userNo") Long userNo, @Param("page") int page, @Param("size") int size);
    BookmarkTypeResponseDto findBookmarkTypeById(@Param("bookmarkTypeNo") Long bookmarkTypeNo);
    void insertBookmark(BookmarkResponseDto bookmarkDto);
    void deleteBookmark(@Param("bookmarkTypeNo") Long bookmarkTypeNo, @Param("attractionNo") Long attractionNo);
    int existsBookmark(@Param("bookmarkTypeNo") Long bookmarkTypeNo, @Param("attractionNo") Long attractionNo);
    void increaseAttractionCount(@Param("bookmarkTypeNo") Long bookmarkTypeNo);
    void decreaseAttractionCount(@Param("bookmarkTypeNo") Long bookmarkTypeNo);
}