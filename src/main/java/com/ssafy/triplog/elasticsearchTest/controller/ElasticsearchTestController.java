package com.ssafy.triplog.elasticsearchTest.controller;

import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.service.ElasticsearchTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    // 성능 비교 테스트 (MySQL vs Elasticsearch)
    @Operation(summary = " 성능 비교 테스트 (MySQL vs Elasticsearch)")
    @GetMapping("/search/ultimate-performance-test")
    public ResponseEntity<Map<String, Object>> ultimatePerformanceTest(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();

        log.warn("성능 비교 테스트 시작 🏆🏆🏆");
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

    @Operation(summary = "🎯 실용적인 핵심 성능 비교 (의미 있는 차이만)")
    @GetMapping("/search/practical-performance-test")
    public ResponseEntity<Map<String, Object>> practicalPerformanceTest(
            @Parameter(description = "검색 키워드 (예: 부산, 서울, 맛집)", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> detailResults = new ArrayList<>();

        log.warn("🎯🎯🎯 실용적인 핵심 성능 비교 시작 🎯🎯🎯");
        log.warn("🔍 검색 키워드: '{}'", keyword);
        log.warn("📊 의미 있는 차이가 나는 6가지 방식만 비교");

        // =========================== 핵심 비교 항목들 ===========================

        // 1️⃣ 🐌 최악의 경우: N+1 문제
        log.info("1️⃣ 【최악 케이스】 N+1 문제 테스트");
        long n1Start = System.currentTimeMillis();
        List<PlanPostResponseDto> n1Result = elasticsearchTestService.searchFourJoinNPlus1(keyword);
        long n1Duration = System.currentTimeMillis() - n1Start;
        addPracticalTestResult(detailResults, "N+1 문제", "MySQL", n1Duration, n1Result.size(),
                "🐌 최악의 안티패턴", "쿼리 " + (1 + n1Result.size()) + "개 실행");
        Thread.sleep(50);

        // 2️⃣ ⚡ JOIN 최적화: 한 번의 쿼리로 해결
        log.info("2️⃣ 【JOIN 최적화】 4중 JOIN 한 번에 해결");
        long joinStart = System.currentTimeMillis();
        List<PlanPostResponseDto> joinResult = elasticsearchTestService.searchByFourJoin(keyword);
        long joinDuration = System.currentTimeMillis() - joinStart;
        addPracticalTestResult(detailResults, "4중 JOIN 최적화", "MySQL", joinDuration, joinResult.size(),
                "⚡ JOIN으로 N+1 해결", "쿼리 1개로 모든 데이터");
        Thread.sleep(50);

        // 3️⃣ 🗃️ 역정규화 (인덱스 없음): 테이블 구조 변경
        log.info("3️⃣ 【역정규화】 캐시 테이블 (인덱스 없음)");
        long cacheStart = System.currentTimeMillis();
        List<PlanPostResponseDto> cacheResult = elasticsearchTestService.searchByCacheLike(keyword);
        long cacheDuration = System.currentTimeMillis() - cacheStart;
        addPracticalTestResult(detailResults, "역정규화 테이블", "MySQL", cacheDuration, cacheResult.size(),
                "🗃️ 테이블 구조 최적화", "조인 없이 단일 테이블");
        Thread.sleep(50);

        // 4️⃣ 🔍 인덱스 효과: 역정규화 + 인덱스
        log.info("4️⃣ 【인덱스 효과】 역정규화 + 통합 컬럼 인덱스");
        long indexedStart = System.currentTimeMillis();
        List<PlanPostResponseDto> indexedResult = elasticsearchTestService.searchByCacheIndexedOptimized(keyword);
        long indexedDuration = System.currentTimeMillis() - indexedStart;
        addPracticalTestResult(detailResults, "인덱스 최적화", "MySQL", indexedDuration, indexedResult.size(),
                "🔍 역정규화 + 인덱스", "단일 컬럼 인덱스 활용");
        Thread.sleep(50);

        // 5️⃣ 🔥 MySQL 전문검색: FULLTEXT 인덱스
        log.info("5️⃣ 【전문 검색】 MySQL FULLTEXT");
        long fulltextStart = System.currentTimeMillis();
        List<PlanPostResponseDto> fulltextResult = elasticsearchTestService.searchByCacheFulltext(keyword);
        long fulltextDuration = System.currentTimeMillis() - fulltextStart;
        addPracticalTestResult(detailResults, "MySQL FULLTEXT", "MySQL", fulltextDuration, fulltextResult.size(),
                "🔥 전문 검색 엔진", "내장 검색 인덱스");
        Thread.sleep(50);

        // 6️⃣ 🚀 전용 검색엔진: Elasticsearch
        log.info("6️⃣ 【검색 엔진】 Elasticsearch Multi-Match");
        long esStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esResult = elasticsearchTestService.searchByElasticsearchMultiMatch(keyword);
        long esDuration = System.currentTimeMillis() - esStart;
        addPracticalTestResult(detailResults, "Elasticsearch", "Elasticsearch", esDuration, esResult.size(),
                "🚀 전용 검색 엔진", "분산 검색 최적화");

        // =========================== 결과 분석 ===========================

        // 성능순 정렬
        detailResults.sort((a, b) -> Long.compare((Long) a.get("duration_ms"), (Long) b.get("duration_ms")));

        // 순위 및 개선 효과 계산
        long baselineTime = (Long) detailResults.stream()
                .filter(r -> "N+1 문제".equals(r.get("method_name")))
                .findFirst().get().get("duration_ms");

        for (int i = 0; i < detailResults.size(); i++) {
            Map<String, Object> testResult = detailResults.get(i);
            testResult.put("rank", i + 1);

            long currentTime = (Long) testResult.get("duration_ms");
            double improvement = ((double) baselineTime / currentTime);
            testResult.put("improvement_ratio", String.format("%.1f", improvement));

            if (improvement > 1) {
                testResult.put("improvement_description", String.format("N+1 대비 %.1f배 빨라짐", improvement));
            } else {
                testResult.put("improvement_description", "기준점 (최악의 경우)");
            }
        }

        // 상세 로깅
        log.warn("📊📊📊 실용적인 성능 비교 결과 📊📊📊");
        log.warn("🔍 검색 키워드: '{}'", keyword);
        log.warn("📈 성능 순위 및 개선 효과:");

        for (Map<String, Object> testResult : detailResults) {
            String emoji = (Integer) testResult.get("rank") == 1 ? "🥇" :
                    (Integer) testResult.get("rank") == 2 ? "🥈" :
                            (Integer) testResult.get("rank") == 3 ? "🥉" : "📍";

            log.warn("{} {}위: {} - {}ms ({}개) | {}",
                    emoji,
                    testResult.get("rank"),
                    testResult.get("method_name"),
                    testResult.get("duration_ms"),
                    testResult.get("result_count"),
                    testResult.get("improvement_description"));
        }

        // 핵심 인사이트
        Map<String, Object> fastest = detailResults.get(0);
        Map<String, Object> slowest = detailResults.get(detailResults.size() - 1);

        long fastestTime = (Long) fastest.get("duration_ms");
        long slowestTime = (Long) slowest.get("duration_ms");
        double totalImprovement = (double) slowestTime / fastestTime;

        log.warn("💡 핵심 인사이트:");
        log.warn("   🏆 최고 성능: {} ({}ms)", fastest.get("method_name"), fastestTime);
        log.warn("   🐌 최저 성능: {} ({}ms)", slowest.get("method_name"), slowestTime);
        log.warn("   ⚡ 성능 차이: {:.1f}배", totalImprovement);
        log.warn("   🎯 권장사항: 검색이 중요하다면 {} 사용 권장", fastest.get("method_name"));

        // 단계별 개선 효과 분석
        log.warn("🔄 단계별 최적화 효과:");
        analyzeOptimizationSteps(detailResults, baselineTime);

        // API 응답 구성
        result.put("keyword", keyword);
        result.put("total_test_count", detailResults.size());
        result.put("test_results", detailResults);

        // 핵심 요약
        result.put("fastest_method", fastest.get("method_name"));
        result.put("fastest_time_ms", fastestTime);
        result.put("slowest_method", slowest.get("method_name"));
        result.put("slowest_time_ms", slowestTime);
        result.put("total_improvement_ratio", String.format("%.1f", totalImprovement));

        // 실용적 권장사항
        result.put("recommendation", generateRecommendation(detailResults));

        log.warn("🎊 실용적인 성능 비교 완료! 의미 있는 차이 확인됨");

        return ResponseEntity.ok(result);
    }

    /**
     * 실용적인 테스트 결과 추가
     */
    private void addPracticalTestResult(List<Map<String, Object>> results, String methodName, String engine,
                                        long duration, int resultCount, String category, String technique) {
        Map<String, Object> testResult = new HashMap<>();
        testResult.put("method_name", methodName);
        testResult.put("engine", engine);
        testResult.put("duration_ms", duration);
        testResult.put("result_count", resultCount);
        testResult.put("category", category);
        testResult.put("technique", technique);
        testResult.put("performance_level", categorizePerformanceLevel(duration));

        results.add(testResult);

        log.info("✅ {} 완료: {}ms ({}개) - {}", methodName, duration, resultCount, category);
    }

    /**
     * 성능 레벨 분류
     */
    private String categorizePerformanceLevel(long durationMs) {
        if (durationMs < 20) return "🚀 최고급";
        else if (durationMs < 50) return "⚡ 우수";
        else if (durationMs < 150) return "🏃 양호";
        else if (durationMs < 500) return "🚶 보통";
        else return "🐌 저조";
    }

    /**
     * 단계별 최적화 효과 분석
     */
    private void analyzeOptimizationSteps(List<Map<String, Object>> results, long baselineTime) {
        // N+1 → JOIN 최적화 효과
        findAndLogImprovement(results, "N+1 문제", "4중 JOIN 최적화", "   📈 1단계 (N+1 → JOIN):");

        // JOIN → 역정규화 효과
        findAndLogImprovement(results, "4중 JOIN 최적화", "역정규화 테이블", "   📈 2단계 (JOIN → 역정규화):");

        // 역정규화 → 인덱스 효과
        findAndLogImprovement(results, "역정규화 테이블", "인덱스 최적화", "   📈 3단계 (역정규화 → 인덱스):");

        // MySQL → Elasticsearch 효과
        findAndLogImprovement(results, "MySQL FULLTEXT", "Elasticsearch", "   📈 4단계 (MySQL → ES):");
    }

    /**
     * 두 방식 간의 개선 효과 로깅
     */
    private void findAndLogImprovement(List<Map<String, Object>> results, String fromMethod, String toMethod, String prefix) {
        var fromResult = results.stream().filter(r -> fromMethod.equals(r.get("method_name"))).findFirst();
        var toResult = results.stream().filter(r -> toMethod.equals(r.get("method_name"))).findFirst();

        if (fromResult.isPresent() && toResult.isPresent()) {
            long fromTime = (Long) fromResult.get().get("duration_ms");
            long toTime = (Long) toResult.get().get("duration_ms");
            double improvement = (double) fromTime / toTime;

            log.warn("{} {}ms → {}ms ({:.1f}배 개선)", prefix, fromTime, toTime, improvement);
        }
    }

    /**
     * 실용적 권장사항 생성
     */
    private String generateRecommendation(List<Map<String, Object>> results) {
        Map<String, Object> fastest = results.get(0);
        String fastestMethod = (String) fastest.get("method_name");
        long fastestTime = (Long) fastest.get("duration_ms");

        if (fastestTime < 20) {
            return fastestMethod + " 사용 권장 - 매우 빠른 성능";
        } else if (fastestTime < 100) {
            return fastestMethod + " 사용 권장 - 양호한 성능";
        } else {
            return "성능 최적화 필요 - 모든 방식이 느림";
        }
    }
    // ElasticsearchTestController.java에 추가할 최종 통합 성능 테스트 API

    @Operation(summary = "🎯 최종 통합 성능 테스트 (MySQL 8가지 + Elasticsearch 3가지 = 총 11가지)")
    @GetMapping("/search/final-ultimate-performance-test")
    public ResponseEntity<Map<String, Object>> finalUltimatePerformanceTest(
            @Parameter(description = "검색 키워드 (예: 부산, 서울, 맛집)", required = true)
            @RequestParam String keyword) throws InterruptedException {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> detailResults = new ArrayList<>();

        log.warn("🎯🎯🎯 최종 통합 성능 테스트 시작 🎯🎯🎯");
        log.warn("🔍 검색 키워드: '{}'", keyword);
        log.warn("📊 총 11가지 검색 방식 비교 실행");

        // =========================== MySQL 검색 방식들 ===========================

        // 1️⃣ N+1 방식 (가장 느림)
        log.info("1️⃣ N+1 방식 테스트 시작");
        long n1Start = System.currentTimeMillis();
        List<PlanPostResponseDto> n1Result = elasticsearchTestService.searchFourJoinNPlus1(keyword);
        long n1Duration = System.currentTimeMillis() - n1Start;
        addTestResult(detailResults, "N+1 방식", "MySQL", n1Duration, n1Result.size(), "가장 비효율적인 방식");
        Thread.sleep(50);

        // 2️⃣ 4중 JOIN 방식
        log.info("2️⃣ 4중 JOIN 방식 테스트 시작");
        long joinStart = System.currentTimeMillis();
        List<PlanPostResponseDto> joinResult = elasticsearchTestService.searchByFourJoin(keyword);
        long joinDuration = System.currentTimeMillis() - joinStart;
        addTestResult(detailResults, "4중 JOIN", "MySQL", joinDuration, joinResult.size(), "복잡한 조인 쿼리");
        Thread.sleep(50);

        // 3️⃣ 캐시 LIKE (인덱스 없음)
        log.info("3️⃣ 캐시 LIKE (인덱스 없음) 테스트 시작");
        long cacheStart = System.currentTimeMillis();
        List<PlanPostResponseDto> cacheResult = elasticsearchTestService.searchByCacheLike(keyword);
        long cacheDuration = System.currentTimeMillis() - cacheStart;
        addTestResult(detailResults, "캐시 LIKE (인덱스X)", "MySQL", cacheDuration, cacheResult.size(), "역정규화 테이블, 인덱스 없음");
        Thread.sleep(50);

        // 4️⃣ 인덱스 캐시 LIKE
        log.info("4️⃣ 인덱스 캐시 LIKE 테스트 시작");
        long indexedStart = System.currentTimeMillis();
        List<PlanPostResponseDto> indexedResult = elasticsearchTestService.searchByCacheIndexed(keyword);
        long indexedDuration = System.currentTimeMillis() - indexedStart;
        addTestResult(detailResults, "인덱스 캐시 LIKE", "MySQL", indexedDuration, indexedResult.size(), "역정규화 + 개별 컬럼 인덱스");
        Thread.sleep(50);

        // 5️⃣ 인덱스 캐시 최적화 (통합 컬럼)
        log.info("5️⃣ 인덱스 캐시 최적화 테스트 시작");
        long optimizedStart = System.currentTimeMillis();
        List<PlanPostResponseDto> optimizedResult = elasticsearchTestService.searchByCacheIndexedOptimized(keyword);
        long optimizedDuration = System.currentTimeMillis() - optimizedStart;
        addTestResult(detailResults, "인덱스 캐시 최적화", "MySQL", optimizedDuration, optimizedResult.size(), "통합 컬럼 + 인덱스");
        Thread.sleep(50);

        // 6️⃣ FULLTEXT 자연어 검색
        log.info("6️⃣ FULLTEXT 자연어 검색 테스트 시작");
        long fulltextStart = System.currentTimeMillis();
        List<PlanPostResponseDto> fulltextResult = elasticsearchTestService.searchByCacheFulltext(keyword);
        long fulltextDuration = System.currentTimeMillis() - fulltextStart;
        addTestResult(detailResults, "FULLTEXT 자연어", "MySQL", fulltextDuration, fulltextResult.size(), "MySQL 내장 전문 검색");
        Thread.sleep(50);

        // 7️⃣ FULLTEXT 불린 검색
        log.info("7️⃣ FULLTEXT 불린 검색 테스트 시작");
        long booleanStart = System.currentTimeMillis();
        List<PlanPostResponseDto> booleanResult = elasticsearchTestService.searchByCacheFulltextBoolean(keyword);
        long booleanDuration = System.currentTimeMillis() - booleanStart;
        addTestResult(detailResults, "FULLTEXT 불린", "MySQL", booleanDuration, booleanResult.size(), "불린 모드 전문 검색");
        Thread.sleep(50);

        // 8️⃣ FULLTEXT 관련도 점수 검색
        log.info("8️⃣ FULLTEXT 관련도 검색 테스트 시작");
        long relevanceStart = System.currentTimeMillis();
        List<PlanPostResponseDto> relevanceResult = elasticsearchTestService.searchByCacheFulltextRelevance(keyword);
        long relevanceDuration = System.currentTimeMillis() - relevanceStart;
        addTestResult(detailResults, "FULLTEXT 관련도", "MySQL", relevanceDuration, relevanceResult.size(), "관련도 점수 기반 정렬");
        Thread.sleep(50);

        // =========================== Elasticsearch 검색 방식들 ===========================

        // 9️⃣ Elasticsearch 쿼리 스트링
        log.info("9️⃣ Elasticsearch 쿼리 스트링 테스트 시작");
        long esQueryStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esQueryResult = elasticsearchTestService.searchByElasticsearch(keyword);
        long esQueryDuration = System.currentTimeMillis() - esQueryStart;
        addTestResult(detailResults, "ES 쿼리 스트링", "Elasticsearch", esQueryDuration, esQueryResult.size(), "Lucene 쿼리 구문");
        Thread.sleep(50);

        // 🔟 Elasticsearch Match 검색
        log.info("🔟 Elasticsearch Match 검색 테스트 시작");
        long esMatchStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esMatchResult = elasticsearchTestService.searchByElasticsearchMatch(keyword);
        long esMatchDuration = System.currentTimeMillis() - esMatchStart;
        addTestResult(detailResults, "ES Match", "Elasticsearch", esMatchDuration, esMatchResult.size(), "단일 필드 매치 검색");
        Thread.sleep(50);

        // 1️⃣1️⃣ Elasticsearch Multi-Match 검색 (최고 성능 예상)
        log.info("1️⃣1️⃣ Elasticsearch Multi-Match 검색 테스트 시작");
        long esMultiStart = System.currentTimeMillis();
        List<PlanPostResponseDto> esMultiResult = elasticsearchTestService.searchByElasticsearchMultiMatch(keyword);
        long esMultiDuration = System.currentTimeMillis() - esMultiStart;
        addTestResult(detailResults, "ES Multi-Match", "Elasticsearch", esMultiDuration, esMultiResult.size(), "다중 필드 가중치 검색");

        // =========================== 결과 분석 및 순위 매기기 ===========================

        // 모든 결과를 성능순으로 정렬
        detailResults.sort((a, b) -> Long.compare((Long) a.get("duration_ms"), (Long) b.get("duration_ms")));

        // 순위 추가
        for (int i = 0; i < detailResults.size(); i++) {
            detailResults.get(i).put("rank", i + 1);
        }

        // 성능 비교 결과 로깅
        log.warn("📊📊📊 최종 통합 성능 테스트 결과 📊📊📊");
        log.warn("🔍 검색 키워드: '{}'", keyword);
        log.warn("📈 성능 순위 (빠른 순서):");

        for (int i = 0; i < detailResults.size(); i++) {
            Map<String, Object> testResult = detailResults.get(i);
            String emoji = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "📍";
            log.warn("{} {}위: {} ({}) - {}ms ({}개 결과)",
                    emoji,
                    testResult.get("rank"),
                    testResult.get("method_name"),
                    testResult.get("engine"),
                    testResult.get("duration_ms"),
                    testResult.get("result_count"));
        }

        // 엔진별 최고 성능 찾기
        Map<String, Object> mysqlBest = detailResults.stream()
                .filter(r -> "MySQL".equals(r.get("engine")))
                .findFirst().orElse(null);

        Map<String, Object> esBest = detailResults.stream()
                .filter(r -> "Elasticsearch".equals(r.get("engine")))
                .findFirst().orElse(null);

        log.warn("🏆 MySQL 최고 성능: {} ({}ms)",
                mysqlBest != null ? mysqlBest.get("method_name") : "N/A",
                mysqlBest != null ? mysqlBest.get("duration_ms") : "N/A");

        log.warn("🚀 Elasticsearch 최고 성능: {} ({}ms)",
                esBest != null ? esBest.get("method_name") : "N/A",
                esBest != null ? esBest.get("duration_ms") : "N/A");

        // 속도 차이 분석
        if (detailResults.size() >= 2) {
            long fastestTime = (Long) detailResults.get(0).get("duration_ms");
            long slowestTime = (Long) detailResults.get(detailResults.size() - 1).get("duration_ms");
            double speedRatio = (double) slowestTime / fastestTime;

            log.warn("⚡ 최고 성능 vs 최저 성능: {:.1f}배 차이", speedRatio);
            log.warn("🎯 최종 우승: {} ({}ms)",
                    detailResults.get(0).get("method_name"),
                    detailResults.get(0).get("duration_ms"));
        }

        // API 응답 구성
        result.put("keyword", keyword);
        result.put("total_test_count", detailResults.size());
        result.put("test_results", detailResults);

        // 요약 정보
        result.put("fastest_method", detailResults.get(0).get("method_name"));
        result.put("fastest_engine", detailResults.get(0).get("engine"));
        result.put("fastest_time_ms", detailResults.get(0).get("duration_ms"));
        result.put("fastest_result_count", detailResults.get(0).get("result_count"));

        result.put("slowest_method", detailResults.get(detailResults.size() - 1).get("method_name"));
        result.put("slowest_engine", detailResults.get(detailResults.size() - 1).get("engine"));
        result.put("slowest_time_ms", detailResults.get(detailResults.size() - 1).get("duration_ms"));

        // MySQL vs Elasticsearch 비교
        result.put("mysql_best_method", mysqlBest != null ? mysqlBest.get("method_name") : null);
        result.put("mysql_best_time_ms", mysqlBest != null ? mysqlBest.get("duration_ms") : null);
        result.put("elasticsearch_best_method", esBest != null ? esBest.get("method_name") : null);
        result.put("elasticsearch_best_time_ms", esBest != null ? esBest.get("duration_ms") : null);

        // 성능 차이
        if (detailResults.size() >= 2) {
            long fastestTime = (Long) detailResults.get(0).get("duration_ms");
            long slowestTime = (Long) detailResults.get(detailResults.size() - 1).get("duration_ms");
            result.put("speed_difference_ratio", String.format("%.1f", (double) slowestTime / fastestTime));
        }

        log.warn("🎊 최종 통합 성능 테스트 완료! 총 {}가지 방식 비교 완료", detailResults.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 테스트 결과를 standardized format으로 추가하는 유틸리티 메소드
     */
    private void addTestResult(List<Map<String, Object>> results, String methodName, String engine,
                               long duration, int resultCount, String description) {
        Map<String, Object> testResult = new HashMap<>();
        testResult.put("method_name", methodName);
        testResult.put("engine", engine);
        testResult.put("duration_ms", duration);
        testResult.put("result_count", resultCount);
        testResult.put("description", description);
        testResult.put("performance_category", categorizePerformance(duration));

        results.add(testResult);

        log.info("✅ {} 완료: {}ms ({}개 결과) - {}", methodName, duration, resultCount, description);
    }

    /**
     * 성능을 카테고리로 분류하는 유틸리티 메소드
     */
    private String categorizePerformance(long durationMs) {
        if (durationMs < 10) return "🚀 매우 빠름";
        else if (durationMs < 30) return "⚡ 빠름";
        else if (durationMs < 100) return "🏃 보통";
        else if (durationMs < 300) return "🚶 느림";
        else return "🐌 매우 느림";
    }
}