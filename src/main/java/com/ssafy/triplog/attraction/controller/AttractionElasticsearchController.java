package com.ssafy.triplog.attraction.controller;

import com.ssafy.triplog.attraction.document.AttractionDocument;
import com.ssafy.triplog.attraction.service.AttractionElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic")
public class AttractionElasticsearchController {

    private final AttractionElasticsearchService esService;

    @PostMapping("/test")
    public ResponseEntity<String> testInsert() throws IOException {
        // 더미 데이터 생성
        AttractionDocument doc = new AttractionDocument(
                1L, "부산 해운대", "부산", "해변이 예쁜 관광지입니다"
        );

        // Elasticsearch에 저장
        esService.save(doc);

        return ResponseEntity.ok("Elasticsearch 저장 성공!");
    }
    @GetMapping("/search")
    public ResponseEntity<List<AttractionDocument>> search(@RequestParam String query) throws IOException {
        List<AttractionDocument> results = esService.search(query);
        return ResponseEntity.ok(results);
    }

}
