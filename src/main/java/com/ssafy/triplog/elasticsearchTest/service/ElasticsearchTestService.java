package com.ssafy.triplog.elasticsearchTest.service;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;

import java.util.List;


public interface ElasticsearchTestService {

    List<PlanPostResponseDto> searchByFourJoin(String keyword);
    List<PlanPostResponseDto> searchFourJoinNPlus1(String keyword);
    List<PlanPostResponseDto> searchByCacheLike(String keyword);
    List<PlanPostResponseDto> searchByCacheIndexed(String keyword);
    List<PlanPostResponseDto> searchByCacheIndexedOptimized(String keyword);
}
