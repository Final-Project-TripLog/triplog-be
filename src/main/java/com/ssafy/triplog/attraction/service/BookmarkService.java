package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.BookmarkResponseDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeResponseDto;

import java.util.List;

/**
 * 북마크 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface BookmarkService {

    /**
     * 사용자의 북마크 타입 목록을 조회합니다.
     *
     * @param userNo 사용자 번호
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 북마크 타입 목록
     */
    List<BookmarkTypeResponseDto> getBookmarkTypesByUser(Long userNo, int page, int size);

    /**
     * 북마크 타입 상세 정보를 조회합니다.
     *
     * @param bookmarkTypeNo 북마크 타입 번호
     * @return 북마크 타입 상세 정보
     * @throws RuntimeException 북마크 타입을 찾을 수 없는 경우
     */
    BookmarkTypeResponseDto getBookmarkTypeDetail(Long bookmarkTypeNo);

    /**
     * 북마크를 추가합니다.
     *
     * @param bookmarkDto 북마크 정보
     * @return 북마크 타입 번호
     * @throws RuntimeException 북마크 타입을 찾을 수 없는 경우
     */
    Long addBookmark(BookmarkResponseDto bookmarkDto);

    /**
     * 북마크를 삭제합니다.
     *
     * @param bookmarkTypeNo 북마크 타입 번호
     * @param attractionNo 관광지 번호
     * @return 북마크 타입 번호
     * @throws RuntimeException 북마크가 존재하지 않는 경우
     */
    Long removeBookmark(Long bookmarkTypeNo, Long attractionNo);
}