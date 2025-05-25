package com.ssafy.triplog.elasticsearch.mapper;

import com.ssafy.triplog.elasticsearch.dto.PlanPostSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElasticsearchMapper {
    List<PlanPostSearchDto> findAllPlanPostsForIndexing();
}
