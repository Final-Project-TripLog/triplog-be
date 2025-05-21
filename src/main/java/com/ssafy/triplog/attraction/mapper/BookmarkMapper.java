// src/main/java/com/ssafy/triplog/attraction/mapper/BookmarkMapper.java
package com.ssafy.triplog.attraction.mapper;

import com.ssafy.triplog.attraction.dto.BookmarkResponseDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookmarkMapper {
    List<BookmarkTypeResponseDto> findBookmarkTypesByUser(
            @Param("userNo") Long userNo,
            @Param("size") int size,
            @Param("offset") int offset  // 'page'를 'offset'으로 변경
    );
    BookmarkTypeResponseDto findBookmarkTypeById(@Param("bookmarkTypeNo") Long bookmarkTypeNo);
    void insertBookmark(BookmarkResponseDto bookmarkDto);
    void deleteBookmark(@Param("bookmarkTypeNo") Long bookmarkTypeNo, @Param("attractionNo") Long attractionNo);
    int existsBookmark(@Param("bookmarkTypeNo") Long bookmarkTypeNo, @Param("attractionNo") Long attractionNo);
    void increaseAttractionCount(@Param("bookmarkTypeNo") Long bookmarkTypeNo);
    void decreaseAttractionCount(@Param("bookmarkTypeNo") Long bookmarkTypeNo);

    void insertBookmarkType(BookmarkTypeDto bookmarkTypeDto);
    Long getLastInsertId();
    // 북마크 타입 수정
    int updateBookmarkType(BookmarkTypeDto bookmarkTypeDto);

    // 북마크 타입 삭제
    int deleteBookmarkType(@Param("bookmarkTypeNo") Long bookmarkTypeNo);

    // 북마크 타입에 속한 모든 북마크 삭제
    void deleteAllBookmarksByType(@Param("bookmarkTypeNo") Long bookmarkTypeNo);

    // 북마크 타입의 마지막 order 값 조회
    int getLastOrderByBookmarkTypeNo(@Param("bookmarkTypeNo") Long bookmarkTypeNo);
}