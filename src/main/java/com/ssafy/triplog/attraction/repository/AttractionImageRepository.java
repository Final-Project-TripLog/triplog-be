package com.ssafy.triplog.attraction.repository;

import com.ssafy.triplog.attraction.dto.AttractionImageDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface AttractionImageRepository {

    // 1. 이미지 등록 (단건)
    int insertImage(AttractionImageDto imageDto);

    // 2. 관광지별 이미지 전체 등록 (일괄 등록)
    int insertImages(List<AttractionImageDto> imageDtoList);

    // 3. 관광지 번호로 이미지 목록 조회
    List<AttractionImageDto> findByAttractionNo(Long attractionNo);

    // 4. 리뷰 번호로 이미지 목록 조회 (리뷰용 이미지 조회)
    List<AttractionImageDto> findByReviewNo(Long reviewNo);

    // 5. 관광지 번호로 전체 이미지 삭제
    int deleteByAttractionNo(Long attractionNo);

    // 6. 리뷰 번호로 이미지 삭제
    int deleteByReviewNo(Long reviewNo);

    // 7. 개별 이미지 삭제
    int deleteById(Long imageNo);
}
