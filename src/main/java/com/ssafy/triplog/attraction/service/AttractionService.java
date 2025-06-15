package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.AttractionImageResponseDto;
import com.ssafy.triplog.attraction.dto.AttractionResponseDto;
//import com.ssafy.triplog.planpost.dto.PlanPostResponse;

import java.util.List;

/**
 * 관광지 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface AttractionService {

    /**
     * 조건에 맞는 관광지 목록을 조회합니다.
     *
     * @param types   관광지 유형 목록 (null 가능)
     * @param keyword 검색 키워드 (null 가능)
     * @param sortBy  정렬 기준 (ratingSum, reviewCount, 기본값은 no)
     * @param page    페이지 번호 (0부터 시작)
     * @param size    페이지 크기
     * @return 관광지 목록
     */
    List<AttractionResponseDto> getAttractions(
            List<String> types, String keyword, String sortBy, int page, int size);

    /**
     * 관광지 상세 정보를 조회합니다.
     *
     * @param attractionNo 관광지 번호
     * @return 관광지 상세 정보
     * @throws RuntimeException 관광지를 찾을 수 없는 경우
     */
    AttractionResponseDto getAttractionDetail(Long attractionNo);

    /**
     * 관광지 이미지 목록을 조회합니다.
     *
     * @param attractionNo 관광지 번호
     * @return 관광지 이미지 목록
     * @throws RuntimeException 관광지를 찾을 수 없는 경우
     */
    List<AttractionImageResponseDto> getAttractionImages(Long attractionNo);

//    /**
//     * 북마크 타입에 포함된 관광지 목록을 조회합니다.
//     *
//     * @param bookmarkTypeNo 북마크 타입 번호
//     * @param page           페이지 번호 (0부터 시작)
//     * @param size           페이지 크기
//     * @return 관광지 목록
//     */
//    List<AttractionResponseDto> getAttractionsByBookmarkType(
//            Long bookmarkTypeNo, int page, int size);

    List<String> getAttractionCategories();

//    /**
//     * 특정 관광지를 포함하는 여행 계획 목록을 조회합니다.
//     *
//     * @param attractionNo 관광지 번호
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 여행 계획 목록
//     */
//    List<PlanPostResponse> getPlansContainingAttraction(
//            Long attractionNo, int page, int size);
}