package com.ssafy.triplog.elasticsearchTest.service;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostTagDto;
import com.ssafy.triplog.elasticsearchTest.mapper.ElasticsearchTestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        log.info("🔥 N+1 방식 키워드 게시글 검색 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        // 1단계: 게시글 목록 조회
        log.info("📊 [쿼리 1] 게시글 목록 조회 실행");
        List<PlanPostDto> posts = elasticsearchTestMapper.searchPlanPostsByKeyword(keyword);
        log.info("✅ [쿼리 1] 완료 - {}개 게시글 조회", posts.size());

        // 2단계: 각 게시글마다 태그 조회 (N+1 발생!)
        log.warn("⚠️  [N+1 구간] {}개 게시글 각각에 대해 태그 조회 시작", posts.size());

        List<PlanPostResponseDto> result = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PlanPostDto dto = posts.get(i);
            int queryNum = i + 2; // 2번째 쿼리부터 시작
            log.debug("🏷️  [쿼리 {}] 게시글 ID {} 태그 조회", queryNum, dto.getNo());
            result.add(convertDtoToResponseDto(dto));
        }

        long endTime = System.currentTimeMillis();
        int totalQueries = 1 + posts.size(); // 1(게시글 조회) + N(각 태그 조회)
        log.error("💥 [N+1 결과] 총 {}개 쿼리 실행, 소요시간: {}ms", totalQueries, endTime - startTime);

        return result;
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

        // 각 게시글마다 별도 쿼리 실행 - N+1 문제 발생
        log.debug("🔍 게시글 ID {} 태그 조회 쿼리 실행 중...", dto.getNo());
        List<PlanPostTagDto> tags = elasticsearchTestMapper.selectPlanPostTagsByPostNo(dto.getNo());
        response.setTags(tags != null ? tags : Collections.emptyList());
        log.debug("✅ 게시글 ID {} 태그 {}개 조회 완료", dto.getNo(), tags != null ? tags.size() : 0);


        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByFourJoin(String keyword) {
        log.info("⚡ [4중 JOIN 테스트] 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] 4중 JOIN 쿼리 실행");
        List<PlanPostResponseDto> posts = elasticsearchTestMapper.searchByFourJoin(keyword);

        long endTime = System.currentTimeMillis();
        log.info("✅ [4중 JOIN 결과] 총 1개 쿼리 실행, {}개 결과, 소요시간: {}ms",
                posts.size(), endTime - startTime);

        return posts;
    }
}