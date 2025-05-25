package com.ssafy.triplog.elasticsearchTest.service;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostTagDto;
import com.ssafy.triplog.elasticsearchTest.mapper.ElasticsearchTestMapper;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("키워드 게시글 검색 - keyword: {}", keyword);

        List<PlanPostDto> posts = elasticsearchTestMapper.searchFourJoinNPlus1(keyword);

        return posts.stream()
                .map(dto -> convertDtoToResponse(dto, dto.getNo()))
                .collect(Collectors.toList());
    }

    private PlanPostResponse convertDtoToResponse(PlanPostDto dto, Long postNo) {
        PlanPostResponse response = new PlanPostResponse();
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

        // 태그 목록 조회
        List<PlanPostTagDto> tags = elasticsearchTestMapper.searchFourJoinNPlus1(postNo);
        response.setTags(tags != null ? tags : Collections.emptyList());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByFourJoin(String keyword) {
        log.info("키워드 게시글 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestMapper.searchByFourJoin(keyword);

        return posts;
    }
}
