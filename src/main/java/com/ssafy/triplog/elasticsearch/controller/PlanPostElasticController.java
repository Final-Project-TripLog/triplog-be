package com.ssafy.triplog.elasticsearch.controller;

import com.ssafy.triplog.elasticsearch.dto.PlanPostSearchDto;
import com.ssafy.triplog.elasticsearch.document.PlanPostDocument;
import com.ssafy.triplog.elasticsearch.mapper.ElasticsearchMapper;
import com.ssafy.triplog.elasticsearch.service.PlanPostElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elastic/planposts")
@RequiredArgsConstructor
public class PlanPostElasticController {

    private final ElasticsearchMapper mapper;
    private final PlanPostElasticsearchService service;

    @PostMapping("/bulk-index")
    // bulk 저장
    public String bulkIndex() throws IOException {
        List<PlanPostSearchDto> list = mapper.findAllPlanPostsForIndexing();

        List<PlanPostDocument> documents = list.stream()
                .map(dto -> new PlanPostDocument(
                        dto.getId(), dto.getTitle(), dto.getDescription(),
                        dto.getUserNickname(), dto.getTags(),
                        dto.getAttractionTitles(), dto.getAddress(), dto.getCreatedAt()
                ))
                .collect(Collectors.toList());

        service.bulkIndex(documents);

        return "✅ Elasticsearch 인덱싱 완료!";
    }
    @GetMapping("/search")
    public List<PlanPostDocument> search(@RequestParam String keyword) throws IOException {
        return service.searchByKeyword(keyword);
    }

}
