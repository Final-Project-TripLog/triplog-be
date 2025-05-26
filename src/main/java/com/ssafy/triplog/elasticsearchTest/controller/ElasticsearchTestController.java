package com.ssafy.triplog.elasticsearchTest.controller;

import com.ssafy.triplog.elasticsearch.dto.PlanPostSearchDto;
import com.ssafy.triplog.elasticsearch.document.PlanPostDocument;
import com.ssafy.triplog.elasticsearch.mapper.ElasticsearchMapper;
import com.ssafy.triplog.elasticsearch.service.PlanPostElasticsearchService;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostResponseDto;
import com.ssafy.triplog.elasticsearchTest.service.ElasticsearchTestService;
import com.ssafy.triplog.elasticsearchTest.service.ElasticsearchTestServiceImpl;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elastic/test/")
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchTestController {

private final ElasticsearchTestService elasticsearchTestService;

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

    @Operation(summary = "캐시 테이블 LIKE 검색")
    @GetMapping("/search/cache-like")
    public ResponseEntity<List<PlanPostResponseDto>> searchCacheLike(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {

        log.info("🗃️ 캐시 테이블 LIKE 검색 - keyword: {}", keyword);

        List<PlanPostResponseDto> posts = elasticsearchTestService.searchByCacheLike(keyword);
        return ResponseEntity.ok(posts);
    }

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
        log.warn("📊📊📊 성능 비교 결과 📊📊📊");
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

}
