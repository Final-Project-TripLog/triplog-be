package com.ssafy.triplog.elasticsearchTest.service;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ElasticsearchTestService {

    List<PlanPostResponseDto> searchByFourJoin(String keyword);
    List<PlanPostResponseDto> searchFourJoinNPlus1(String keyword);

}
