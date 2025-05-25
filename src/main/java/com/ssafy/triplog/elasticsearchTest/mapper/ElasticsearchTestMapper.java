package com.ssafy.triplog.elasticsearchTest.mapper;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElasticsearchTestMapper {
    List<PlanPostResponseDto> searchByFourJoin(@Param("keyword") String keyword);
    List<PlanPostDto> searchPlanPostsByKeyword(@Param("keyword") String keyword);
}
