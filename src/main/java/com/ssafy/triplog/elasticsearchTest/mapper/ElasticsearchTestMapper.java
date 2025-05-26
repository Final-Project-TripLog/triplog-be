// 🔧 ElasticsearchTestMapper.java (수정된 버전)
package com.ssafy.triplog.elasticsearchTest.mapper;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostTagDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostCacheDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostCacheIndexedDto; // 🆕 추가
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElasticsearchTestMapper {
    // 기존 메소드들
    List<PlanPostResponseDto> searchByFourJoin(@Param("keyword") String keyword);
    List<PlanPostDto> searchPlanPostsByKeyword(@Param("keyword") String keyword);
    List<PlanPostTagDto> selectPlanPostTagsByPostNo(@Param("postNo") Long postNo);

    // 🗃️ 캐시 테이블 검색 메소드 (인덱스 없음)
    List<PlanPostCacheDto> searchByCache(@Param("keyword") String keyword);

    // 🔍 인덱스 캐시 테이블 검색 메소드들 (수정된 반환 타입)
    List<PlanPostCacheIndexedDto> searchByCacheIndexed(@Param("keyword") String keyword);
    List<PlanPostCacheIndexedDto> searchByCacheIndexedOptimized(@Param("keyword") String keyword);
}