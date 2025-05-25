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
import java.util.List;
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

}
