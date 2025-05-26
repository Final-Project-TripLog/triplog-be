package com.ssafy.triplog.elasticsearchTest.controller;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.service.ElasticsearchTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elastic/test/")
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchTestController {

    private final ElasticsearchTestService elasticsearchTestService;

    // ✅ 기존 API들
    @Operation(summary = "4중 JOIN으로 검색 + N+1")
    @GetMapping("/search/four-join-N+1")
    public ResponseEntity<List<PlanPostResponseDto>> searchFourJoinNPlus1(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword){

        log.info("키워드 게시글 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchFourJoinNPlus1(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "4중 JOIN으로 검색")
    @GetMapping("/search/four-join")
    public ResponseEntity<List<PlanPostResponseDto>> searchFourJoin(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword){

        log.info("키워드 게시글 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByFourJoin(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "캐시 테이블 LIKE 검색 (인덱스 없음)")
    @GetMapping("/search/cache-like")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheLike(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🗃️ 캐시 테이블 LIKE 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheLike(keyword);
        return ResponseEntity.ok(posts);
    }

    // 🆕 누락된 API들 추가
    @Operation(summary = "인덱스 캐시 테이블 LIKE 검색")
    @GetMapping("/search/cache-indexed")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheIndexed(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🔍 인덱스 캐시 테이블 LIKE 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheIndexed(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "인덱스 캐시 테이블 최적화 검색")
    @GetMapping("/search/cache-indexed-optimized")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheIndexedOptimized(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🚀 인덱스 캐시 테이블 최적화 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheIndexedOptimized(keyword);
        return ResponseEntity.ok(posts);
    }

    // 🔄 기존 성능 비교 (3가지)
    @Operation(summary = "성능 비교 테스트 (3가지 방식)")
    @GetMapping("/search/performance-comparison")
    public ResponseEntity<Map<String, Object>> performanceComparison(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();

        // 1. N+1 방식 테스트
        log.info("🔥🔥🔥 N+1 테스트 시작 🔥🔥🔥");
        long n1StartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> n1Result = elasticsearchTestService.searchFourJoinNPlus1(keyword);
        long n1Duration = System.currentTimeMillis() - n1StartTime;
        log.info("🔥 N+1 완료: {}ms ({}개)", n1Duration, n1Result.size());

        Thread.sleep(50); // 구분용 대기

        // 2. 4중 JOIN 방식 테스트
        log.info("⚡⚡⚡ 4중 JOIN 테스트 시작 ⚡⚡⚡");
        long joinStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> joinResult = elasticsearchTestService.searchByFourJoin(keyword);
        long joinDuration = System.currentTimeMillis() - joinStartTime;
        log.info("⚡ 4중 JOIN 완료: {}ms ({}개)", joinDuration, joinResult.size());

        Thread.sleep(50); // 구분용 대기

        // 3. 캐시 테이블 LIKE 방식 테스트
        log.info("🗃️🗃️🗃️ 캐시 LIKE 테스트 시작 🗃️🗃️🗃️");
        long cacheStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> cacheResult = elasticsearchTestService.searchByCacheLike(keyword);
        long cacheDuration = System.currentTimeMillis() - cacheStartTime;
        log.info("🗃️ 캐시 LIKE 완료: {}ms ({}개)", cacheDuration, cacheResult.size());

        // 성능 비교 결과 로깅
        log.warn("📊📊📊 성능 비교 결과 (3가지) 📊📊📊");
        log.warn("N+1 방식:         {}ms ({}개 결과)", n1Duration, n1Result.size());
        log.warn("4중 JOIN:         {}ms ({}개 결과)", joinDuration, joinResult.size());
        log.warn("캐시 LIKE:        {}ms ({}개 결과)", cacheDuration, cacheResult.size());

        // 가장 빠른 방식 찾기
        long[] times = {n1Duration, joinDuration, cacheDuration};
        String[] methods = {"N+1", "4중 JOIN", "캐시 LIKE"};

        long fastestTime = Arrays.stream(times).min().orElse(0);
        String fastestMethod = "";
        for (int i = 0; i < times.length; i++) {
            if (times[i] == fastestTime) {
                fastestMethod = methods[i];
                break;
            }
        }

        log.warn("🏆 가장 빠른 방식: {} ({}ms)", fastestMethod, fastestTime);

        result.put("n1_time_ms", n1Duration);
        result.put("n1_result_count", n1Result.size());
        result.put("join_time_ms", joinDuration);
        result.put("join_result_count", joinResult.size());
        result.put("cache_like_time_ms", cacheDuration);
        result.put("cache_like_result_count", cacheResult.size());
        result.put("fastest_method", fastestMethod);
        result.put("fastest_time_ms", fastestTime);

        return ResponseEntity.ok(result);
    }

    // 🏆 전체 성능 비교 (5가지 모든 방식)
    @Operation(summary = "전체 성능 비교 테스트 (5가지 모든 방식)")
    @GetMapping("/search/performance-comparison-all")
    public ResponseEntity<Map<String, Object>> performanceComparisonAll(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();

        // 1. N+1 방식
        log.info("🔥🔥🔥 N+1 테스트 시작 🔥🔥🔥");
        long n1StartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> n1Result = elasticsearchTestService.searchFourJoinNPlus1(keyword);
        long n1Duration = System.currentTimeMillis() - n1StartTime;
        log.info("🔥 N+1 완료: {}ms ({}개)", n1Duration, n1Result.size());

        Thread.sleep(50);

        // 2. 4중 JOIN 방식
        log.info("⚡⚡⚡ 4중 JOIN 테스트 시작 ⚡⚡⚡");
        long joinStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> joinResult = elasticsearchTestService.searchByFourJoin(keyword);
        long joinDuration = System.currentTimeMillis() - joinStartTime;
        log.info("⚡ 4중 JOIN 완료: {}ms ({}개)", joinDuration, joinResult.size());

        Thread.sleep(50);

        // 3. 캐시 LIKE (인덱스 없음)
        log.info("🗃️🗃️🗃️ 캐시 LIKE (인덱스 없음) 테스트 시작 🗃️🗃️🗃️");
        long cacheStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> cacheResult = elasticsearchTestService.searchByCacheLike(keyword);
        long cacheDuration = System.currentTimeMillis() - cacheStartTime;
        log.info("🗃️ 캐시 LIKE (인덱스 없음) 완료: {}ms ({}개)", cacheDuration, cacheResult.size());

        Thread.sleep(50);

        // 4. 인덱스 캐시 LIKE
        log.info("🔍🔍🔍 인덱스 캐시 LIKE 테스트 시작 🔍🔍🔍");
        long indexedStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> indexedResult = elasticsearchTestService.searchByCacheIndexed(keyword);
        long indexedDuration = System.currentTimeMillis() - indexedStartTime;
        log.info("🔍 인덱스 캐시 LIKE 완료: {}ms ({}개)", indexedDuration, indexedResult.size());

        Thread.sleep(50);

        // 5. 인덱스 캐시 최적화
        log.info("🚀🚀🚀 인덱스 캐시 최적화 테스트 시작 🚀🚀🚀");
        long optimizedStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> optimizedResult = elasticsearchTestService.searchByCacheIndexedOptimized(keyword);
        long optimizedDuration = System.currentTimeMillis() - optimizedStartTime;
        log.info("🚀 인덱스 캐시 최적화 완료: {}ms ({}개)", optimizedDuration, optimizedResult.size());

        // 성능 비교 결과
        log.warn("🏆🏆🏆 전체 성능 비교 결과 (5가지) 🏆🏆🏆");
        log.warn("1️⃣ N+1 방식:             {}ms ({}개 결과)", n1Duration, n1Result.size());
        log.warn("2️⃣ 4중 JOIN:             {}ms ({}개 결과)", joinDuration, joinResult.size());
        log.warn("3️⃣ 캐시 LIKE (인덱스X):   {}ms ({}개 결과)", cacheDuration, cacheResult.size());
        log.warn("4️⃣ 인덱스 캐시 LIKE:      {}ms ({}개 결과)", indexedDuration, indexedResult.size());
        log.warn("5️⃣ 인덱스 캐시 최적화:    {}ms ({}개 결과)", optimizedDuration, optimizedResult.size());

        // 가장 빠른 방식 찾기
        long[] times = {n1Duration, joinDuration, cacheDuration, indexedDuration, optimizedDuration};
        String[] methods = {"N+1", "4중 JOIN", "캐시 LIKE (인덱스X)", "인덱스 캐시 LIKE", "인덱스 캐시 최적화"};

        long fastestTime = Arrays.stream(times).min().orElse(0);
        String fastestMethod = "";
        for (int i = 0; i < times.length; i++) {
            if (times[i] == fastestTime) {
                fastestMethod = methods[i];
                break;
            }
        }

        log.warn("🏆 가장 빠른 방식: {} ({}ms)", fastestMethod, fastestTime);

        result.put("n1_time_ms", n1Duration);
        result.put("n1_result_count", n1Result.size());
        result.put("join_time_ms", joinDuration);
        result.put("join_result_count", joinResult.size());
        result.put("cache_like_time_ms", cacheDuration);
        result.put("cache_like_result_count", cacheResult.size());
        result.put("cache_indexed_time_ms", indexedDuration);
        result.put("cache_indexed_result_count", indexedResult.size());
        result.put("cache_indexed_optimized_time_ms", optimizedDuration);
        result.put("cache_indexed_optimized_result_count", optimizedResult.size());
        result.put("fastest_method", fastestMethod);
        result.put("fastest_time_ms", fastestTime);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "FULLTEXT 자연어 검색")
    @GetMapping("/search/cache-fulltext")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheFulltext(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🔍 FULLTEXT 자연어 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheFulltext(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "FULLTEXT 불린 검색")
    @GetMapping("/search/cache-fulltext-boolean")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheFulltextBoolean(
            @Parameter(description = "검색 키워드 (불린 문법: +word, -word)", required = true)
            @RequestParam String keyword) {

        log.info("🎯 FULLTEXT 불린 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheFulltextBoolean(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "FULLTEXT 관련도 점수 검색")
    @GetMapping("/search/cache-fulltext-relevance")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheFulltextRelevance(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🌟 FULLTEXT 관련도 점수 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheFulltextRelevance(keyword);
        return ResponseEntity.ok(posts);
    }

    // 🏆 최종 성능 비교 메소드 (8가지 모든 방식)
    @Operation(summary = "최종 성능 비교 테스트 (8가지 모든 방식)")
    @GetMapping("/search/performance-comparison-final")
    public ResponseEntity<Map<String, Object>> performanceComparisonFinal(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();

        // 기존 5가지 방식들...
        // 1. N+1 방식
        log.info("🔥🔥🔥 N+1 테스트 시작 🔥🔥🔥");
        long n1StartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> n1Result = elasticsearchTestService.searchFourJoinNPlus1(keyword);
        long n1Duration = System.currentTimeMillis() - n1StartTime;
        Thread.sleep(50);

        // 2. 4중 JOIN 방식
        log.info("⚡⚡⚡ 4중 JOIN 테스트 시작 ⚡⚡⚡");
        long joinStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> joinResult = elasticsearchTestService.searchByFourJoin(keyword);
        long joinDuration = System.currentTimeMillis() - joinStartTime;
        Thread.sleep(50);

        // 3. 캐시 LIKE (인덱스 없음)
        long cacheStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> cacheResult = elasticsearchTestService.searchByCacheLike(keyword);
        long cacheDuration = System.currentTimeMillis() - cacheStartTime;
        Thread.sleep(50);

        // 4. 인덱스 캐시 LIKE
        long indexedStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> indexedResult = elasticsearchTestService.searchByCacheIndexed(keyword);
        long indexedDuration = System.currentTimeMillis() - indexedStartTime;
        Thread.sleep(50);

        // 5. 인덱스 캐시 최적화
        long optimizedStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> optimizedResult = elasticsearchTestService.searchByCacheIndexedOptimized(keyword);
        long optimizedDuration = System.currentTimeMillis() - optimizedStartTime;
        Thread.sleep(50);

        // 🆕 6. FULLTEXT 자연어 검색
        log.info("🔍🔍🔍 FULLTEXT 자연어 테스트 시작 🔍🔍🔍");
        long fulltextStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> fulltextResult = elasticsearchTestService.searchByCacheFulltext(keyword);
        long fulltextDuration = System.currentTimeMillis() - fulltextStartTime;
        Thread.sleep(50);

        // 🆕 7. FULLTEXT 불린 검색
        log.info("🎯🎯🎯 FULLTEXT 불린 테스트 시작 🎯🎯🎯");
        long booleanStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> booleanResult = elasticsearchTestService.searchByCacheFulltextBoolean(keyword);
        long booleanDuration = System.currentTimeMillis() - booleanStartTime;
        Thread.sleep(50);

        // 🆕 8. FULLTEXT 관련도 검색
        log.info("🌟🌟🌟 FULLTEXT 관련도 테스트 시작 🌟🌟🌟");
        long relevanceStartTime = System.currentTimeMillis();
        List<PlanPostResponseDto> relevanceResult = elasticsearchTestService.searchByCacheFulltextRelevance(keyword);
        long relevanceDuration = System.currentTimeMillis() - relevanceStartTime;

        // 성능 비교 결과
        log.warn("🏆🏆🏆 최종 성능 비교 결과 (8가지 모든 방식) 🏆🏆🏆");
        log.warn("1️⃣ N+1 방식:                {}ms ({}개 결과)", n1Duration, n1Result.size());
        log.warn("2️⃣ 4중 JOIN:                {}ms ({}개 결과)", joinDuration, joinResult.size());
        log.warn("3️⃣ 캐시 LIKE (인덱스X):      {}ms ({}개 결과)", cacheDuration, cacheResult.size());
        log.warn("4️⃣ 인덱스 캐시 LIKE:         {}ms ({}개 결과)", indexedDuration, indexedResult.size());
        log.warn("5️⃣ 인덱스 캐시 최적화:       {}ms ({}개 결과)", optimizedDuration, optimizedResult.size());
        log.warn("6️⃣ FULLTEXT 자연어:         {}ms ({}개 결과)", fulltextDuration, fulltextResult.size());
        log.warn("7️⃣ FULLTEXT 불린:           {}ms ({}개 결과)", booleanDuration, booleanResult.size());
        log.warn("8️⃣ FULLTEXT 관련도:         {}ms ({}개 결과)", relevanceDuration, relevanceResult.size());

        // 가장 빠른 방식 찾기
        long[] times = {n1Duration, joinDuration, cacheDuration, indexedDuration, optimizedDuration,
                fulltextDuration, booleanDuration, relevanceDuration};
        String[] methods = {"N+1", "4중 JOIN", "캐시 LIKE (인덱스X)", "인덱스 캐시 LIKE", "인덱스 캐시 최적화",
                "FULLTEXT 자연어", "FULLTEXT 불린", "FULLTEXT 관련도"};

        long fastestTime = Arrays.stream(times).min().orElse(0);
        String fastestMethod = "";
        for (int i = 0; i < times.length; i++) {
            if (times[i] == fastestTime) {
                fastestMethod = methods[i];
                break;
            }
        }

        log.warn("🏆 가장 빠른 방식: {} ({}ms)", fastestMethod, fastestTime);

        // 결과 설정
        result.put("n1_time_ms", n1Duration);
        result.put("join_time_ms", joinDuration);
        result.put("cache_like_time_ms", cacheDuration);
        result.put("cache_indexed_time_ms", indexedDuration);
        result.put("cache_indexed_optimized_time_ms", optimizedDuration);
        result.put("fulltext_natural_time_ms", fulltextDuration);
        result.put("fulltext_boolean_time_ms", booleanDuration);
        result.put("fulltext_relevance_time_ms", relevanceDuration);
        result.put("fastest_method", fastestMethod);
        result.put("fastest_time_ms", fastestTime);

        return ResponseEntity.ok(result);
    }


// 🆕 Elasticsearch 검색 엔드포인트들

    @Operation(summary = "Elasticsearch 쿼리 스트링 검색")
    @GetMapping("/search/elasticsearch")
    public ResponseEntity<List<PlanPostResponseDto>> searchElasticsearch(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🔍 Elasticsearch 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByElasticsearch(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Elasticsearch Match 검색")
    @GetMapping("/search/elasticsearch-match")
    public ResponseEntity<List<PlanPostResponseDto>> searchElasticsearchMatch(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🔍 Elasticsearch Match 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByElasticsearchMatch(keyword);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Elasticsearch Multi-Match 검색")
    @GetMapping("/search/elasticsearch-multi")
    public ResponseEntity<List<PlanPostResponseDto>> searchElasticsearchMultiMatch(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🔍 Elasticsearch Multi-Match 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByElasticsearchMultiMatch(keyword);
        return ResponseEntity.ok(posts);
    }

    // 🏆 궁극의 성능 비교 테스트 (MySQL vs Elasticsearch)
    @Operation(summary = "🏆 궁극의 성능 비교 테스트 (MySQL vs Elasticsearch)")
    @GetMapping("/search/ultimate-performance-test")
    public ResponseEntity<Map<String, Object>> ultimatePerformanceTest(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();

        log.warn("🏆🏆🏆 궁극의 성능 비교 테스트 시작 🏆🏆🏆");
        log.warn("키워드: {}", keyword);

        // 1. MySQL LIKE 검색 (최적화됨)
        log.info("🗃️ MySQL 인덱스 캐시 최적화 검색 시작");
        long mysqlOptimizedStart = System.currentTimeMillis();
        List<PlanPostResponseDto> mysqlOptimizedResult = elasticsearchTestService.searchByCacheIndexedOptimized(keyword);
        long mysqlOptimizedDuration = System.currentTimeMillis() - mysqlOptimizedStart;
        log.info("🗃️ MySQL 최적화 완료: {}ms ({}개)", mysqlOptimizedDuration, mysqlOptimizedResult.size());

        Thread.sleep(100);

        // 2. MySQL FULLTEXT 검색
        log.info("🔍 MySQL FULLTEXT 자연어 검색 시작");
        long mysqlFulltextStart = System.currentTimeMillis();
        List<PlanPostResponseDto> mysqlFulltextResult = elasticsearchTestService.searchByCacheFulltext(keyword);
        long mysqlFulltextDuration = System.currentTimeMillis() - mysqlFulltextStart;
        log.info("🔍 MySQL FULLTEXT 완료: {}ms ({}개)", mysqlFulltextDuration, mysqlFulltextResult.size());

        Thread.sleep(100);

        // 3. MySQL 4중 JOIN 검색
        log.info("⚡ MySQL 4중 JOIN 검색 시작");
        long mysql4JoinStart = System.currentTimeMillis();
        List<PlanPostResponseDto> mysql4JoinResult = elasticsearchTestService.searchByFourJoin(keyword);
        long mysql4JoinDuration = System.currentTimeMillis() - mysql4JoinStart;
        log.info("⚡ MySQL 4중 JOIN 완료: {}ms ({}개)", mysql4JoinDuration, mysql4JoinResult.size());

        Thread.sleep(100);

        // 4. Elasticsearch Query String 검색
        log.info("🚀 Elasticsearch 쿼리 스트링 검색 시작");
        long esQueryStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esQueryResult = elasticsearchTestService.searchByElasticsearch(keyword);
        long esQueryDuration = System.currentTimeMillis() - esQueryStart;
        log.info("🚀 Elasticsearch 쿼리 스트링 완료: {}ms ({}개)", esQueryDuration, esQueryResult.size());

        Thread.sleep(100);

        // 5. Elasticsearch Match 검색
        log.info("🎯 Elasticsearch Match 검색 시작");
        long esMatchStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esMatchResult = elasticsearchTestService.searchByElasticsearchMatch(keyword);
        long esMatchDuration = System.currentTimeMillis() - esMatchStart;
        log.info("🎯 Elasticsearch Match 완료: {}ms ({}개)", esMatchDuration, esMatchResult.size());

        Thread.sleep(100);

        // 6. Elasticsearch Multi-Match 검색
        log.info("⭐ Elasticsearch Multi-Match 검색 시작");
        long esMultiStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esMultiResult = elasticsearchTestService.searchByElasticsearchMultiMatch(keyword);
        long esMultiDuration = System.currentTimeMillis() - esMultiStart;
        log.info("⭐ Elasticsearch Multi-Match 완료: {}ms ({}개)", esMultiDuration, esMultiResult.size());

        // 성능 비교 결과 로깅
        log.warn("📊📊📊 궁극의 성능 비교 결과 📊📊📊");
        log.warn("1️⃣ MySQL 인덱스 최적화:        {}ms ({}개 결과)", mysqlOptimizedDuration, mysqlOptimizedResult.size());
        log.warn("2️⃣ MySQL FULLTEXT:            {}ms ({}개 결과)", mysqlFulltextDuration, mysqlFulltextResult.size());
        log.warn("3️⃣ MySQL 4중 JOIN:            {}ms ({}개 결과)", mysql4JoinDuration, mysql4JoinResult.size());
        log.warn("4️⃣ Elasticsearch 쿼리:        {}ms ({}개 결과)", esQueryDuration, esQueryResult.size());
        log.warn("5️⃣ Elasticsearch Match:       {}ms ({}개 결과)", esMatchDuration, esMatchResult.size());
        log.warn("6️⃣ Elasticsearch Multi-Match: {}ms ({}개 결과)", esMultiDuration, esMultiResult.size());

        // 가장 빠른 방식 찾기
        long[] times = {mysqlOptimizedDuration, mysqlFulltextDuration, mysql4JoinDuration,
                esQueryDuration, esMatchDuration, esMultiDuration};
        String[] methods = {"MySQL 인덱스 최적화", "MySQL FULLTEXT", "MySQL 4중 JOIN",
                "ES 쿼리", "ES Match", "ES Multi-Match"};

        long fastestTime = Arrays.stream(times).min().orElse(0);
        String fastestMethod = "";
        for (int i = 0; i < times.length; i++) {
            if (times[i] == fastestTime) {
                fastestMethod = methods[i];
                break;
            }
        }

        log.warn("🏆 가장 빠른 방식: {} ({}ms)", fastestMethod, fastestTime);

        // 결과 정리
        result.put("keyword", keyword);
        result.put("mysql_optimized_time_ms", mysqlOptimizedDuration);
        result.put("mysql_optimized_result_count", mysqlOptimizedResult.size());
        result.put("mysql_fulltext_time_ms", mysqlFulltextDuration);
        result.put("mysql_fulltext_result_count", mysqlFulltextResult.size());
        result.put("mysql_4join_time_ms", mysql4JoinDuration);
        result.put("mysql_4join_result_count", mysql4JoinResult.size());
        result.put("elasticsearch_query_time_ms", esQueryDuration);
        result.put("elasticsearch_query_result_count", esQueryResult.size());
        result.put("elasticsearch_match_time_ms", esMatchDuration);
        result.put("elasticsearch_match_result_count", esMatchResult.size());
        result.put("elasticsearch_multi_time_ms", esMultiDuration);
        result.put("elasticsearch_multi_result_count", esMultiResult.size());
        result.put("fastest_method", fastestMethod);
        result.put("fastest_time_ms", fastestTime);

        return ResponseEntity.ok(result);
    }

}