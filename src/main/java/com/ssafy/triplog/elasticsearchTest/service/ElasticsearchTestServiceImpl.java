package com.ssafy.triplog.elasticsearchTest.service;

import com.ssafy.triplog.elasticsearchTest.dto.*;
import com.ssafy.triplog.elasticsearchTest.mapper.ElasticsearchTestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
    // 캐시 테이블 LIKE 검색 (사용자 테이블 구조)
    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheLike(String keyword) {
        log.info("🗃️ [캐시 테이블 LIKE] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] 캐시 테이블 LIKE 검색 실행");
        List<PlanPostCacheDto> cacheDtos = elasticsearchTestMapper.searchByCache(keyword);

        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheDtoToResponseDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("✅ [캐시 LIKE] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    /**
     * 캐시 DTO를 ResponseDto로 변환 (사용자 테이블 구조)
     */
    private PlanPostResponseDto convertCacheDtoToResponseDto(PlanPostCacheDto cacheDto) {
        PlanPostResponseDto response = new PlanPostResponseDto();

        // 기본 필드 매핑
        response.setNo(cacheDto.getNo());
        response.setUserNo(cacheDto.getUserNo());
        response.setUserNickname(cacheDto.getUserNickname());
        response.setTitle(cacheDto.getTitle());
        response.setDescription(cacheDto.getDescription());
        response.setThumbnail(cacheDto.getThumbnail());
        response.setCreatedAt(cacheDto.getCreatedAt());
        response.setUpdatedAt(cacheDto.getUpdatedAt());
        response.setStartDay(cacheDto.getStartDay());
        response.setEndDay(cacheDto.getEndDay());
        response.setTotalMember(cacheDto.getTotalMember().longValue()); // INT → Long 변환
        response.setForkCount(cacheDto.getForkCount());
        response.setLikedCount(cacheDto.getLikedCount());
        response.setViewCount(cacheDto.getViewCount());

        // 🔥 콤마 구분된 태그를 List로 변환 (사용자 테이블: tags 컬럼)
        List<PlanPostTagDto> tags = convertTagNamesToTagDtos(cacheDto.getTags(), cacheDto.getNo());
        response.setTags(tags);

        log.debug("🏷️ 게시글 ID {} - 태그 {}개 변환: [{}]",
                cacheDto.getNo(), tags.size(),
                tags.stream().map(PlanPostTagDto::getName).collect(Collectors.joining(", ")));

        log.debug("🎯 관광지: [{}]", cacheDto.getAttractionTitles());
        log.debug("📍 주소: [{}]", cacheDto.getAddresses());

        return response;
    }

    /**
     * 콤마 구분된 태그명을 PlanPostTagDto 리스트로 변환
     */
    private List<PlanPostTagDto> convertTagNamesToTagDtos(String tagNames, Long planPostNo) {
        if (tagNames == null || tagNames.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(tagNames.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(name -> {
                    PlanPostTagDto tagDto = new PlanPostTagDto();
                    tagDto.setNo(null); // 캐시에서는 태그 PK를 저장하지 않음
                    tagDto.setPlanPostNo(planPostNo);
                    tagDto.setName(name);
                    return tagDto;
                })
                .collect(Collectors.toList());
    }

    // 인덱스 캐시 테이블 각 컬럼별 LIKE 검색
    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheIndexed(String keyword) {
        log.info("🔍 [인덱스 캐시 LIKE] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] 인덱스 캐시 테이블 각 컬럼별 LIKE 검색 실행");
        List<PlanPostCacheIndexedDto> cacheDtos = elasticsearchTestMapper.searchByCacheIndexed(keyword);

        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheIndexedDtoToResponseDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("✅ [인덱스 캐시 LIKE] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    // 인덱스 캐시 테이블 통합 컬럼 LIKE 검색 (최적화)
    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheIndexedOptimized(String keyword) {
        log.info("🚀 [인덱스 캐시 최적화] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] 인덱스 캐시 테이블 통합 컬럼 검색 실행");
        List<PlanPostCacheIndexedDto> cacheDtos = elasticsearchTestMapper.searchByCacheIndexedOptimized(keyword);

        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheIndexedDtoToResponseDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("✅ [인덱스 캐시 최적화] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    /**
     * 인덱스 캐시 DTO를 ResponseDto로 변환
     */
    private PlanPostResponseDto convertCacheIndexedDtoToResponseDto(PlanPostCacheIndexedDto cacheDto) {
        PlanPostResponseDto response = new PlanPostResponseDto();

        // 기본 필드 매핑
        response.setNo(cacheDto.getNo());
        response.setUserNo(cacheDto.getUserNo());
        response.setUserNickname(cacheDto.getUserNickname());
        response.setTitle(cacheDto.getTitle());
        response.setDescription(cacheDto.getDescription());
        response.setThumbnail(cacheDto.getThumbnail());
        response.setCreatedAt(cacheDto.getCreatedAt());
        response.setUpdatedAt(cacheDto.getUpdatedAt());
        response.setStartDay(cacheDto.getStartDay());
        response.setEndDay(cacheDto.getEndDay());
        response.setTotalMember(cacheDto.getTotalMember().longValue());
        response.setForkCount(cacheDto.getForkCount());
        response.setLikedCount(cacheDto.getLikedCount());
        response.setViewCount(cacheDto.getViewCount());

        // 🔥 콤마 구분된 태그를 List로 변환
        List<PlanPostTagDto> tags = convertTagNamesToTagDtos(cacheDto.getTags(), cacheDto.getNo());
        response.setTags(tags);

        log.debug("🏷️ [인덱스 캐시] 게시글 ID {} - 태그 {}개 변환: [{}]",
                cacheDto.getNo(), tags.size(),
                tags.stream().map(PlanPostTagDto::getName).collect(Collectors.joining(", ")));

        return response;
    }

    // FULLTEXT 자연어 검색
    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheFulltext(String keyword) {
        log.info("🔍 [FULLTEXT 자연어] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] FULLTEXT 자연어 검색 실행");
        List<PlanPostCacheFulltextDto> cacheDtos = elasticsearchTestMapper.searchByCacheFulltext(keyword);

        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheFulltextDtoToResponseDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("✅ [FULLTEXT 자연어] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    // FULLTEXT 불린 검색
    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheFulltextBoolean(String keyword) {
        log.info("🎯 [FULLTEXT 불린] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] FULLTEXT 불린 검색 실행");
        List<PlanPostCacheFulltextDto> cacheDtos = elasticsearchTestMapper.searchByCacheFulltextBoolean(keyword);

        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheFulltextDtoToResponseDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("✅ [FULLTEXT 불린] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    // FULLTEXT 관련도 점수 검색
    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheFulltextRelevance(String keyword) {
        log.info("🌟 [FULLTEXT 관련도] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        log.info("📊 [쿼리 1] FULLTEXT 관련도 점수 검색 실행");
        List<PlanPostCacheFulltextDto> cacheDtos = elasticsearchTestMapper.searchByCacheFulltextRelevance(keyword);

        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheFulltextDtoToResponseDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("✅ [FULLTEXT 관련도] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    /**
     * FULLTEXT 캐시 DTO를 ResponseDto로 변환
     */
    private PlanPostResponseDto convertCacheFulltextDtoToResponseDto(PlanPostCacheFulltextDto cacheDto) {
        PlanPostResponseDto response = new PlanPostResponseDto();

        // 기본 필드 매핑
        response.setNo(cacheDto.getNo());
        response.setUserNo(cacheDto.getUserNo());
        response.setUserNickname(cacheDto.getUserNickname());
        response.setTitle(cacheDto.getTitle());
        response.setDescription(cacheDto.getDescription());
        response.setThumbnail(cacheDto.getThumbnail());
        response.setCreatedAt(cacheDto.getCreatedAt());
        response.setUpdatedAt(cacheDto.getUpdatedAt());
        response.setStartDay(cacheDto.getStartDay());
        response.setEndDay(cacheDto.getEndDay());
        response.setTotalMember(cacheDto.getTotalMember().longValue());
        response.setForkCount(cacheDto.getForkCount());
        response.setLikedCount(cacheDto.getLikedCount());
        response.setViewCount(cacheDto.getViewCount());

        // 🔥 콤마 구분된 태그를 List로 변환
        List<PlanPostTagDto> tags = convertTagNamesToTagDtos(cacheDto.getTags(), cacheDto.getNo());
        response.setTags(tags);

        log.debug("🌟 [FULLTEXT] 게시글 ID {} - 관련도 점수: {}, 태그 {}개: [{}]",
                cacheDto.getNo(),
                cacheDto.getRelevanceScore(),
                tags.size(),
                tags.stream().map(PlanPostTagDto::getName).collect(Collectors.joining(", ")));

        return response;
    }

}