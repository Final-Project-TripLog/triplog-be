package com.ssafy.triplog.elasticsearchTest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.triplog.elasticsearchTest.dto.*;
import com.ssafy.triplog.elasticsearchTest.mapper.ElasticsearchTestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ElasticsearchTestServiceImpl implements ElasticsearchTestService {

    private final ElasticsearchTestMapper elasticsearchTestMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private static final String INDEX_NAME = "plan_posts";

    // ========== 기존 MySQL 검색 메소드들 ==========

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchFourJoinNPlus1(String keyword) {
        log.info("🔥 N+1 방식 키워드 게시글 검색 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        List<PlanPostDto> posts = elasticsearchTestMapper.searchPlanPostsByKeyword(keyword);
        log.info("✅ [쿼리 1] 완료 - {}개 게시글 조회", posts.size());

        List<PlanPostResponseDto> result = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PlanPostDto dto = posts.get(i);
            result.add(convertDtoToResponseDto(dto));
        }

        long endTime = System.currentTimeMillis();
        int totalQueries = 1 + posts.size();
        log.error("💥 [N+1 결과] 총 {}개 쿼리 실행, 소요시간: {}ms", totalQueries, endTime - startTime);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByFourJoin(String keyword) {
        log.info("⚡ [4중 JOIN 테스트] 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostResponseDto> posts = elasticsearchTestMapper.searchByFourJoin(keyword);
        long endTime = System.currentTimeMillis();

        log.info("✅ [4중 JOIN 결과] 총 1개 쿼리 실행, {}개 결과, 소요시간: {}ms",
                posts.size(), endTime - startTime);

        return posts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheLike(String keyword) {
        log.info("🗃️ [캐시 테이블 LIKE] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostCacheDto> cacheDtos = elasticsearchTestMapper.searchByCache(keyword);
        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheDtoToResponseDto)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info("✅ [캐시 LIKE] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheIndexed(String keyword) {
        log.info("🔍 [인덱스 캐시 LIKE] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostCacheIndexedDto> cacheDtos = elasticsearchTestMapper.searchByCacheIndexed(keyword);
        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheIndexedDtoToResponseDto)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info("✅ [인덱스 캐시 LIKE] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheIndexedOptimized(String keyword) {
        log.info("🚀 [인덱스 캐시 최적화] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostCacheIndexedDto> cacheDtos = elasticsearchTestMapper.searchByCacheIndexedOptimized(keyword);
        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheIndexedDtoToResponseDto)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info("✅ [인덱스 캐시 최적화] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheFulltext(String keyword) {
        log.info("🔍 [FULLTEXT 자연어] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostCacheFulltextDto> cacheDtos = elasticsearchTestMapper.searchByCacheFulltext(keyword);
        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheFulltextDtoToResponseDto)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info("✅ [FULLTEXT 자연어] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheFulltextBoolean(String keyword) {
        log.info("🎯 [FULLTEXT 불린] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostCacheFulltextDto> cacheDtos = elasticsearchTestMapper.searchByCacheFulltextBoolean(keyword);
        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheFulltextDtoToResponseDto)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info("✅ [FULLTEXT 불린] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByCacheFulltextRelevance(String keyword) {
        log.info("🌟 [FULLTEXT 관련도] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();
        List<PlanPostCacheFulltextDto> cacheDtos = elasticsearchTestMapper.searchByCacheFulltextRelevance(keyword);
        List<PlanPostResponseDto> result = cacheDtos.stream()
                .map(this::convertCacheFulltextDtoToResponseDto)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info("✅ [FULLTEXT 관련도] 완료 - 총 1개 쿼리, {}개 결과, {}ms",
                result.size(), endTime - startTime);

        return result;
    }

    // ========== 🆕 Elasticsearch 검색 메소드들 ==========

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByElasticsearch(String keyword) {
        log.info("🔍 [Elasticsearch 쿼리 스트링] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            sourceBuilder.query(QueryBuilders.queryStringQuery(keyword)
                    .field("search_all")
                    .defaultOperator(Operator.OR));

            sourceBuilder.size(1000);
            sourceBuilder.sort("created_at", SortOrder.DESC);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<PlanPostResponseDto> result = convertSearchHitsToResponseDto(searchResponse.getHits());

            long endTime = System.currentTimeMillis();
            log.info("✅ [Elasticsearch 쿼리 스트링] 완료 - {}개 결과, {}ms", result.size(), endTime - startTime);

            return result;

        } catch (IOException e) {
            log.error("💥 Elasticsearch 검색 오류", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByElasticsearchMatch(String keyword) {
        log.info("🔍 [Elasticsearch Match] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            sourceBuilder.query(QueryBuilders.matchQuery("search_all", keyword));
            sourceBuilder.size(1000);
            sourceBuilder.sort("_score", SortOrder.DESC);
            sourceBuilder.sort("created_at", SortOrder.DESC);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<PlanPostResponseDto> result = convertSearchHitsToResponseDto(searchResponse.getHits());

            long endTime = System.currentTimeMillis();
            log.info("✅ [Elasticsearch Match] 완료 - {}개 결과, {}ms", result.size(), endTime - startTime);

            return result;

        } catch (IOException e) {
            log.error("💥 Elasticsearch Match 검색 오류", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponseDto> searchByElasticsearchMultiMatch(String keyword) {
        log.info("🔍 [Elasticsearch Multi-Match] 검색 시작 - keyword: {}", keyword);

        long startTime = System.currentTimeMillis();

        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword)
                    .field("title", 2.0f)
                    .field("user_nickname", 1.5f)
                    .field("tags", 1.8f)
                    .field("attraction_titles", 1.3f)
                    .field("addresses", 1.0f)
                    .field("description", 0.8f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS));

            sourceBuilder.size(1000);
            sourceBuilder.sort("_score", SortOrder.DESC);
            sourceBuilder.sort("created_at", SortOrder.DESC);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<PlanPostResponseDto> result = convertSearchHitsToResponseDto(searchResponse.getHits());

            long endTime = System.currentTimeMillis();
            log.info("✅ [Elasticsearch Multi-Match] 완료 - {}개 결과, {}ms", result.size(), endTime - startTime);

            return result;

        } catch (IOException e) {
            log.error("💥 Elasticsearch Multi-Match 검색 오류", e);
            return Collections.emptyList();
        }
    }

    // ========== 유틸리티 메소드들 ==========

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

        List<PlanPostTagDto> tags = elasticsearchTestMapper.selectPlanPostTagsByPostNo(dto.getNo());
        response.setTags(tags != null ? tags : Collections.emptyList());

        return response;
    }

    private PlanPostResponseDto convertCacheDtoToResponseDto(PlanPostCacheDto cacheDto) {
        PlanPostResponseDto response = new PlanPostResponseDto();

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

        List<PlanPostTagDto> tags = convertTagNamesToTagDtos(cacheDto.getTags(), cacheDto.getNo());
        response.setTags(tags);

        return response;
    }

    private PlanPostResponseDto convertCacheIndexedDtoToResponseDto(PlanPostCacheIndexedDto cacheDto) {
        PlanPostResponseDto response = new PlanPostResponseDto();

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

        List<PlanPostTagDto> tags = convertTagNamesToTagDtos(cacheDto.getTags(), cacheDto.getNo());
        response.setTags(tags);

        return response;
    }

    private PlanPostResponseDto convertCacheFulltextDtoToResponseDto(PlanPostCacheFulltextDto cacheDto) {
        PlanPostResponseDto response = new PlanPostResponseDto();

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

        List<PlanPostTagDto> tags = convertTagNamesToTagDtos(cacheDto.getTags(), cacheDto.getNo());
        response.setTags(tags);

        return response;
    }

    private List<PlanPostTagDto> convertTagNamesToTagDtos(String tagNames, Long planPostNo) {
        if (tagNames == null || tagNames.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(tagNames.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(name -> {
                    PlanPostTagDto tagDto = new PlanPostTagDto();
                    tagDto.setNo(null);
                    tagDto.setPlanPostNo(planPostNo);
                    tagDto.setName(name);
                    return tagDto;
                })
                .collect(Collectors.toList());
    }

    private List<PlanPostResponseDto> convertSearchHitsToResponseDto(SearchHits hits) {
        List<PlanPostResponseDto> result = new ArrayList<>();

        for (SearchHit hit : hits.getHits()) {
            try {
                Map<String, Object> source = hit.getSourceAsMap();
                PlanPostResponseDto responseDto = convertMapToResponseDto(source);
                result.add(responseDto);

                log.debug("🔍 ES 문서 변환 - ID: {}, Score: {}, Title: {}",
                        hit.getId(), hit.getScore(), source.get("title"));

            } catch (Exception e) {
                log.error("💥 Elasticsearch 문서 변환 오류 - ID: {}", hit.getId(), e);
            }
        }

        return result;
    }

    private PlanPostResponseDto convertMapToResponseDto(Map<String, Object> source) {
        PlanPostResponseDto response = new PlanPostResponseDto();

        response.setNo(((Number) source.get("no")).longValue());
        response.setUserNo(((Number) source.get("user_no")).longValue());
        response.setUserNickname((String) source.get("user_nickname"));
        response.setTitle((String) source.get("title"));
        response.setDescription((String) source.get("description"));
        response.setThumbnail((String) source.get("thumbnail"));

        if (source.get("created_at") != null) {
            response.setCreatedAt(parseLocalDateTime(source.get("created_at")));
        }
        if (source.get("updated_at") != null) {
            response.setUpdatedAt(parseLocalDateTime(source.get("updated_at")));
        }
        if (source.get("start_day") != null) {
            response.setStartDay(parseLocalDateTime(source.get("start_day")));
        }
        if (source.get("end_day") != null) {
            response.setEndDay(parseLocalDateTime(source.get("end_day")));
        }

        if (source.get("total_member") != null) {
            response.setTotalMember(((Number) source.get("total_member")).longValue());
        }
        if (source.get("fork_count") != null) {
            response.setForkCount(((Number) source.get("fork_count")).intValue());
        }
        if (source.get("liked_count") != null) {
            response.setLikedCount(((Number) source.get("liked_count")).intValue());
        }
        if (source.get("view_count") != null) {
            response.setViewCount(((Number) source.get("view_count")).intValue());
        }

        String tags = (String) source.get("tags");
        List<PlanPostTagDto> tagList = convertTagNamesToTagDtos(tags, response.getNo());
        response.setTags(tagList);

        return response;
    }

    private LocalDateTime parseLocalDateTime(Object dateObj) {
        if (dateObj == null) return null;

        try {
            if (dateObj instanceof String) {
                String dateStr = (String) dateObj;
                return LocalDateTime.parse(dateStr.replace("Z", ""));
            } else if (dateObj instanceof Long) {
                return LocalDateTime.ofInstant(
                        Instant.ofEpochMilli((Long) dateObj),
                        ZoneId.systemDefault()
                );
            }
        } catch (Exception e) {
            log.warn("⚠️ 날짜 파싱 실패: {}", dateObj, e);
        }

        return null;
    }
}