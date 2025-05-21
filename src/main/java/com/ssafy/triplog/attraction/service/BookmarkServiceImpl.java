package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.BookmarkResponseDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeResponseDto;
import com.ssafy.triplog.attraction.mapper.BookmarkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkMapper bookmarkMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BookmarkTypeResponseDto> getBookmarkTypesByUser(Long userNo, int page, int size) {
        log.debug("getBookmarkTypesByUser: userNo={}, page={}, size={}", userNo, page, size);

        return bookmarkMapper.findBookmarkTypesByUser(userNo, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public BookmarkTypeResponseDto getBookmarkTypeDetail(Long bookmarkTypeNo) {
        log.debug("getBookmarkTypeDetail: bookmarkTypeNo={}", bookmarkTypeNo);

        BookmarkTypeResponseDto bookmarkType = bookmarkMapper.findBookmarkTypeById(bookmarkTypeNo);
        if (bookmarkType == null) {
            throw new RuntimeException("북마크 타입을 찾을 수 없습니다: " + bookmarkTypeNo);
        }

        return bookmarkType;
    }

    @Override
    @Transactional
    public Long addBookmark(BookmarkResponseDto bookmarkDto) {
        log.debug("addBookmark: {}", bookmarkDto);

        // 북마크 타입 존재 여부 확인
        if (bookmarkMapper.findBookmarkTypeById(bookmarkDto.getBookmarkTypeNo()) == null) {
            throw new RuntimeException("북마크 타입을 찾을 수 없습니다: " + bookmarkDto.getBookmarkTypeNo());
        }

        // 북마크 추가
        bookmarkMapper.insertBookmark(bookmarkDto);

        // 북마크 타입의 관광지 수 증가
        bookmarkMapper.increaseAttractionCount(bookmarkDto.getBookmarkTypeNo());

        return bookmarkDto.getBookmarkTypeNo();
    }

    @Override
    @Transactional
    public Long removeBookmark(Long bookmarkTypeNo, Long attractionNo) {
        log.debug("removeBookmark: bookmarkTypeNo={}, attractionNo={}", bookmarkTypeNo, attractionNo);

        // 북마크 존재 여부 확인
        if (bookmarkMapper.existsBookmark(bookmarkTypeNo, attractionNo) == 0) {
            throw new RuntimeException("해당 북마크가 존재하지 않습니다.");
        }

        // 북마크 삭제
        bookmarkMapper.deleteBookmark(bookmarkTypeNo, attractionNo);

        // 북마크 타입의 관광지 수 감소
        bookmarkMapper.decreaseAttractionCount(bookmarkTypeNo);

        return bookmarkTypeNo;
    }
}