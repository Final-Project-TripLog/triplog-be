package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.AttractionReviewResponseDto;

import java.util.List;

/**
 * 리뷰 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ReviewService {

    /**
     * 관광지별 리뷰 목록을 조회합니다.
     *
     * @param attractionNo 관광지 번호
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록
     * @throws RuntimeException 관광지를 찾을 수 없는 경우
     */
    List<AttractionReviewResponseDto> getReviewsByAttraction(
            Long attractionNo, int page, int size);

    /**
     * 사용자별 리뷰 목록을 조회합니다.
     *
     * @param userNo 사용자 번호
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록
     */
    List<AttractionReviewResponseDto> getReviewsByUser(Long userNo, int page, int size);

    /**
     * 리뷰를 등록합니다.
     *
     * @param reviewDto 리뷰 정보
     * @return 관광지 번호
     * @throws RuntimeException 관광지를 찾을 수 없는 경우
     */
    Long addReview(AttractionReviewResponseDto reviewDto);

    /**
     * 리뷰를 수정합니다.
     *
     * @param reviewDto 리뷰 정보
     * @return 관광지 번호
     * @throws RuntimeException 리뷰를 찾을 수 없는 경우
     */
    Long updateReview(AttractionReviewResponseDto reviewDto);

    /**
     * 리뷰를 삭제합니다.
     *
     * @param reviewNo 리뷰 번호
     * @throws RuntimeException 리뷰를 찾을 수 없는 경우
     */
    void deleteReview(Long reviewNo);

    // ReviewService 인터페이스에 추가
    AttractionReviewResponseDto findReviewById(Long reviewNo);


}