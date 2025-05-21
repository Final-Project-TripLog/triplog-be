package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.BookmarkResponseDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeDto;
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

        // 페이지 번호를 오프셋으로 변환
        int offset = page * size;

        return bookmarkMapper.findBookmarkTypesByUser(userNo, size, offset);
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

        // 이미 해당 북마크 타입에 관광지가 등록되어 있는지 확인
        if (bookmarkMapper.existsBookmark(bookmarkDto.getBookmarkTypeNo(), bookmarkDto.getAttractionNo()) > 0) {
            throw new RuntimeException("이미 북마크에 등록된 관광지입니다.");
        }

        // 마지막 order 값 조회 후 +1 설정
        int lastOrder = bookmarkMapper.getLastOrderByBookmarkTypeNo(bookmarkDto.getBookmarkTypeNo());
        bookmarkDto.setOrder(lastOrder + 1);

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


    @Override
    @Transactional
    public BookmarkTypeResponseDto createBookmarkType(BookmarkTypeDto bookmarkTypeDto) {
        // 북마크 타입 생성
        bookmarkMapper.insertBookmarkType(bookmarkTypeDto);

        // 생성된 북마크 타입 정보 조회
        Long bookmarkTypeNo = bookmarkMapper.getLastInsertId();
        return bookmarkMapper.findBookmarkTypeById(bookmarkTypeNo);
    }

    @Override
    @Transactional
    public BookmarkTypeResponseDto updateBookmarkType(BookmarkTypeDto bookmarkTypeDto) {
        // 북마크 타입 존재 여부 확인
        if (bookmarkMapper.findBookmarkTypeById(bookmarkTypeDto.getNo()) == null) {
            throw new RuntimeException("북마크 타입을 찾을 수 없습니다: " + bookmarkTypeDto.getNo());
        }

        // 북마크 타입 수정
        bookmarkMapper.updateBookmarkType(bookmarkTypeDto);

        // 수정된 북마크 타입 정보 조회
        return bookmarkMapper.findBookmarkTypeById(bookmarkTypeDto.getNo());
    }

    @Override
    @Transactional
    public boolean deleteBookmarkType(Long bookmarkTypeNo) {
        // 북마크 타입 존재 여부 확인
        if (bookmarkMapper.findBookmarkTypeById(bookmarkTypeNo) == null) {
            throw new RuntimeException("북마크 타입을 찾을 수 없습니다: " + bookmarkTypeNo);
        }

        // 해당 북마크 타입에 속한 모든 북마크 삭제
        bookmarkMapper.deleteAllBookmarksByType(bookmarkTypeNo);

        // 북마크 타입 삭제
        return bookmarkMapper.deleteBookmarkType(bookmarkTypeNo) > 0;
    }
}