package com.ssafy.triplog.elasticsearchTest.service;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostTagDto;
import com.ssafy.triplog.elasticsearchTest.mapper.ElasticsearchTestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ElasticsearchTestServiceImpl implements ElasticsearchTestService {

    private final ElasticsearchTestMapper elasticsearchTestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchFourJoinNPlus1(String keyword) {
        log.info("N+1 방식 키워드 게시글 검색 - keyword: {}", keyword);

        // 1. 먼저 게시글 목록만 조회 (N+1 문제 발생 지점)
        List<PlanPostDto> posts = elasticsearchTestMapper.searchPlanPostsByKeyword(keyword);

        // 2. 각 게시글마다 별도로 태그 조회 (N+1 문제!)
        return posts.stream()
                .map(dto -> convertDtoToResponseDto(dto))
                .collect(Collectors.toList());
    }

    private PlanPostResponseDto convertDtoToResponseDto(PlanPostDto dto) {
        PlanPostResponseDto response = new PlanPostResponseDto();
        response.setNo(dto.getNo());
        response.setUserNo(dto.getUserNo());
        response.setUserNickname(dto.getUserNickname());
        response.setTitle(dto.getTitle());
        response.setDescription(dto.getDescription());
        response.setThumbnail(dto.getThumbnail());
        response.setCreatedAt(dto.getCreatedAt());
        response.setUpdatedAt(dto.getUpdatedAt());
        response.setStartDay(dto.getStartDay());
        response.setEndDay(dto.getEndDay());
        response.setTotalMember(dto.getTotalMember());
        response.setForkCount(dto.getForkCount());
        response.setLikedCount(dto.getLikedCount());
        response.setViewCount(dto.getViewCount());

        // 각 게시글마다 별도 쿼리 실행 - N+1 문제 발생!
        List<PlanPostTagDto> tags = elasticsearchTestMapper.selectPlanPostTagsByPostNo(dto.getNo());
        response.setTags(tags != null ? tags : Collections.emptyList());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByFourJoin(String keyword) {
        log.info("4중 JOIN 방식 키워드 게시글 검색 - keyword: {}", keyword);

        // 한 번의 쿼리로 모든 데이터 조회 (N+1 문제 해결)
        List<PlanPostResponseDto> posts = elasticsearchTestMapper.searchByFourJoin(keyword);

        return posts;
    }
}