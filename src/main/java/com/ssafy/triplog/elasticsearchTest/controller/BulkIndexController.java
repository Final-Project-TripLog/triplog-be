package com.ssafy.triplog.elasticsearchTest.controller;

import com.ssafy.triplog.elasticsearchTest.service.ElasticsearchBulkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/elastic/bulk/")
@RequiredArgsConstructor
@Slf4j
public class BulkIndexController {

    private final ElasticsearchBulkService elasticsearchBulkService;

    @Operation(summary = "MySQL 데이터를 Elasticsearch에 색인")
    @PostMapping("/index-all")
    public ResponseEntity<Map<String, Object>> indexAllData() {
        log.info("🚀 MySQL → Elasticsearch 데이터 색인 시작");

        try {
            long startTime = System.currentTimeMillis();

            // MySQL에서 데이터 조회 후 Elasticsearch에 색인
            int indexedCount = elasticsearchBulkService.indexAllPlanPosts();

            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("indexed_count", indexedCount);
            result.put("duration_ms", duration);
            result.put("status", "SUCCESS");

            log.info("✅ 색인 완료: {}개 문서, {}ms", indexedCount, duration);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("💥 색인 중 오류 발생", e);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("error_message", e.getMessage());

            return ResponseEntity.internalServerError().body(result);
        }
    }

    @Operation(summary = "Elasticsearch 인덱스 삭제")
    @DeleteMapping("/delete-index")
    public ResponseEntity<Map<String, Object>> deleteIndex() {
        log.info("🗑️ Elasticsearch 인덱스 삭제");

        try {
            elasticsearchBulkService.deleteIndex();

            Map<String, Object> result = new HashMap<>();
            result.put("status", "SUCCESS");
            result.put("message", "인덱스가 삭제되었습니다");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("💥 인덱스 삭제 중 오류 발생", e);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("error_message", e.getMessage());

            return ResponseEntity.internalServerError().body(result);
        }
    }

    @Operation(summary = "Elasticsearch 연결 테스트")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            boolean isHealthy = elasticsearchBulkService.checkHealth();

            Map<String, Object> result = new HashMap<>();
            result.put("elasticsearch_status", isHealthy ? "UP" : "DOWN");
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("elasticsearch_status", "ERROR");
            result.put("error_message", e.getMessage());

            return ResponseEntity.internalServerError().body(result);
        }
    }

}