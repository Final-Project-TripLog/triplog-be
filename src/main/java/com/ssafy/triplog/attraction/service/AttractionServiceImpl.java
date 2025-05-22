package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.AttractionImageResponseDto;
import com.ssafy.triplog.attraction.dto.AttractionResponseDto;
import com.ssafy.triplog.attraction.mapper.AttractionMapper;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AttractionResponseDto> getAttractions(List<String> types, String keyword, String sortBy, int page, int size) {
        log.debug("getAttractions: types={}, keyword={}, sortBy={}, page={}, size={}",
                types, keyword, sortBy, page, size);

        // 페이지 번호를 오프셋으로 변환
        int offset = page * size;
        // sortBy 값 검증 및 기본값 설정
        String validSortBy = "ratingSum".equals(sortBy) || "reviewCount".equals(sortBy)
                ? sortBy : "no";

        return attractionMapper.findAttractions(types, keyword, validSortBy, size, offset);
    }

    @Override
    @Transactional(readOnly = true)
    public AttractionResponseDto getAttractionDetail(Long attractionNo) {
        log.debug("getAttractionDetail: attractionNo={}", attractionNo);

        AttractionResponseDto attraction = attractionMapper.findById(attractionNo);
        if (attraction == null) {
            throw new RuntimeException("관광지 정보를 찾을 수 없습니다: " + attractionNo);
        }

        return attraction;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionImageResponseDto> getAttractionImages(Long attractionNo) {
        log.debug("getAttractionImages: attractionNo={}", attractionNo);

        // 관광지 존재 여부 확인
        if (attractionMapper.findById(attractionNo) == null) {
            throw new RuntimeException("관광지 정보를 찾을 수 없습니다: " + attractionNo);
        }

        return attractionMapper.findImagesByAttractionId(attractionNo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionResponseDto> getAttractionsByBookmarkType(Long bookmarkTypeNo, int page, int size) {
        log.debug("getAttractionsByBookmarkType: bookmarkTypeNo={}, page={}, size={}",
                bookmarkTypeNo, page, size);

        // 페이지 번호를 오프셋으로 변환
        int offset = page * size;

        return attractionMapper.findAttractionsByBookmarkType(bookmarkTypeNo, size, offset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponse> getPlansContainingAttraction(Long attractionNo, int page, int size) {
        log.debug("getPlansContainingAttraction: attractionNo={}, page={}, size={}",
                attractionNo, page, size);
        // 관광지 존재 여부 확인
        if (attractionMapper.findById(attractionNo) == null) {
            throw new RuntimeException("관광지 정보를 찾을 수 없습니다: " + attractionNo);
        }
        // 페이지를 오프셋으로 변환
        int offset = page * size;
        // 관광지를 포함하는 여행 계획 조회
        return attractionMapper.findPlansByAttractionId(attractionNo, size, offset);
    }

}