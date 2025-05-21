package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.AttractionReviewResponseDto;
import com.ssafy.triplog.attraction.mapper.AttractionMapper;
import com.ssafy.triplog.attraction.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final AttractionMapper attractionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AttractionReviewResponseDto> getReviewsByAttraction(Long attractionNo, int page, int size) {
        log.debug("getReviewsByAttraction: attractionNo={}, page={}, size={}",
                attractionNo, page, size);

        // 페이지 오프셋 계산 (페이지 기반 → 오프셋 기반으로 변환)
        int offset = page * size;
        // 관광지 존재 여부 확인
        if (attractionMapper.findById(attractionNo) == null) {
            throw new RuntimeException("관광지 정보를 찾을 수 없습니다: " + attractionNo);
        }

        return reviewMapper.findReviewsByAttraction(attractionNo, size, offset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionReviewResponseDto> getReviewsByUser(Long userNo, int page, int size) {
        log.debug("getReviewsByUser: userNo={}, page={}, size={}", userNo, page, size);

        return reviewMapper.findReviewsByUser(userNo, page, size);
    }

    @Override
    @Transactional
    public Long addReview(AttractionReviewResponseDto reviewDto) {
        log.debug("addReview: {}", reviewDto);

        // 관광지 존재 여부 확인
        if (attractionMapper.findById(reviewDto.getAttractionNo()) == null) {
            throw new RuntimeException("관광지 정보를 찾을 수 없습니다: " + reviewDto.getAttractionNo());
        }

        // 현재 시간 설정
        reviewDto.setCreateAt(LocalDateTime.now());
        reviewDto.setUpdateAt(LocalDateTime.now());

        // 리뷰 등록
        reviewMapper.insertReview(reviewDto);

        // 관광지 평점 업데이트
        updateAttractionRating(reviewDto.getAttractionNo(), reviewDto.getRating(), true);

        return reviewDto.getAttractionNo();
    }

    @Override
    @Transactional
    public Long updateReview(AttractionReviewResponseDto reviewDto) {
        log.debug("updateReview: {}", reviewDto);

        // 리뷰 존재 여부 확인
        AttractionReviewResponseDto existingReview = reviewMapper.findReviewById(reviewDto.getNo());
        if (existingReview == null) {
            throw new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewDto.getNo());
        }

        // 기존 평점 저장 (관광지 평점 업데이트에 사용)
        Integer oldRating = existingReview.getRating();

        // 수정 시간 업데이트
        reviewDto.setUpdateAt(LocalDateTime.now());

        // 리뷰 수정
        reviewMapper.updateReview(reviewDto);

        // 관광지 평점 업데이트 (기존 평점 제거 후 새 평점 추가)
        Long attractionNo = existingReview.getAttractionNo();
        updateAttractionRating(attractionNo, oldRating, false);
        updateAttractionRating(attractionNo, reviewDto.getRating(), true);

        return attractionNo;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewNo) {
        log.debug("deleteReview: reviewNo={}", reviewNo);

        // 리뷰 존재 여부 확인
        AttractionReviewResponseDto review = reviewMapper.findReviewById(reviewNo);
        if (review == null) {
            throw new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewNo);
        }

        // 리뷰 삭제
        reviewMapper.deleteReview(reviewNo);

        // 관광지 평점 업데이트 (평점 제거)
        updateAttractionRating(review.getAttractionNo(), review.getRating(), false);
    }

    // 관광지 평점 업데이트 (평점 추가 또는 제거)
    private void updateAttractionRating(Long attractionNo, Integer rating, boolean isAdd) {
        if (isAdd) {
            // 평점 추가
            attractionMapper.increaseRatingSum(attractionNo, rating);
            attractionMapper.increaseReviewCount(attractionNo);
        } else {
            // 평점 제거
            attractionMapper.decreaseRatingSum(attractionNo, rating);
            attractionMapper.decreaseReviewCount(attractionNo);
        }
    }
}