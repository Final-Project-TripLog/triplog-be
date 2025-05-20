package com.ssafy.triplog.attraction.repository;

import com.ssafy.triplog.attraction.dto.AttractionDto;
import com.ssafy.triplog.attraction.dto.AttractionPreviewResponse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface AttractionRepository {

    // 1. 관광지 등록
    Long insertAttraction(AttractionDto dto);

    // 2. 관광지 수정
    int update(Long attractionNo, AttractionDto dto);  // 변경된 행 수 반환

    // 3. 관광지 삭제
    int deleteById(Long attractionNo);  // 삭제된 행 수 반환

    // 4. 관광지 상세 조회
    AttractionDto findById(Long attractionNo);

    // 5. 관광지 리스트 조회 (조건 검색)
    List<AttractionPreviewResponse> searchAttractions(
            List<Integer> types,
            Integer sidoNo,
            Integer gugunNo,
            String keyword,
            String sortBy,
            int page,
            int size
    );

    // 6. 총 개수 조회 (페이지네이션을 위한)
    int countSearchResults(List<Integer> types, Integer sidoNo, Integer gugunNo, String keyword);

    // 7. 주변 관광지 조회
    List<AttractionPreviewResponse> findNearby(
            Double latitude,
            Double longitude,
            Double distanceKm,
            int page,
            int size
    );
}
