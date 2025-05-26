package com.ssafy.triplog.elasticsearch.controller;

import com.ssafy.triplog.elasticsearch.dto.PlanPostSearchDto;
import com.ssafy.triplog.elasticsearch.document.PlanPostDocument;
import com.ssafy.triplog.elasticsearch.mapper.ElasticsearchMapper;
import com.ssafy.triplog.elasticsearch.service.PlanPostElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elastic/planposts")
@RequiredArgsConstructor
@Slf4j
public class PlanPostElasticController {

    private final ElasticsearchMapper mapper;
    private final PlanPostElasticsearchService service;

    //  테스트용 엔드포인트 추가
    @GetMapping("/test")
    public String test() {
        log.info("Elasticsearch 테스트 엔드포인트 호출됨");
        return "✅ Elasticsearch API 테스트 성공!";
    }

    // 인덱스 상태 확인 엔드포인트 추가
    @GetMapping("/status")
    public String getIndexStatus() {
        log.info("인덱스 상태 확인 요청");
        return service.getIndexStatus();
    }

    //  인덱스 삭제 엔드포인트 (테스트용)
    @DeleteMapping("/index")
    public String deleteIndex() {
        log.info("인덱스 삭제 요청");
        try {
            service.deleteIndex();
            return "✅ 인덱스 삭제 완료";
        } catch (Exception e) {
            log.error("인덱스 삭제 실패", e);
            return "❌ 인덱스 삭제 실패: " + e.getMessage();
        }
    }

    @PostMapping("/bulk-index")
    // bulk 저장
    public String bulkIndex() {
        log.info("Elasticsearch bulk-index 요청 받음");

        try {
            List<PlanPostSearchDto> list = mapper.findAllPlanPostsForIndexing();
            log.info("DB에서 조회된 데이터 수: {}", list.size());

            List<PlanPostDocument> documents = list.stream()
                    .map(dto -> new PlanPostDocument(
                            dto.getId(), dto.getTitle(), dto.getDescription(),
                            dto.getUserNickname(), dto.getTags(),
                            dto.getAttractionTitles(), dto.getAddress(), dto.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            service.bulkIndex(documents);
            log.info("Elasticsearch 인덱싱 처리 완료");

            //  인덱싱 후 상태 확인
            String status = service.getIndexStatus();
            return "✅ Elasticsearch 인덱싱 처리 완료! 처리된 문서 수: " + documents.size() +
                    "\n" + status;
        } catch (IOException e) {
            log.error("Elasticsearch 인덱싱 중 오류 발생", e);
            return "❌ Elasticsearch 인덱싱 중 오류 발생: " + e.getMessage();
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return "❌ 예상치 못한 오류 발생: " + e.getMessage();
        }
    }

    @GetMapping("/search")
    public List<PlanPostDocument> search(@RequestParam String keyword) {
        log.info("Elasticsearch 검색 요청 - 키워드: {}", keyword);

        try {
            List<PlanPostDocument> results = service.searchByKeyword(keyword);
            log.info("검색 결과 수: {}", results.size());
            return results;
        } catch (IOException e) {
            log.error("Elasticsearch 검색 중 오류 발생", e);
            throw new RuntimeException("검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //  헬스체크 엔드포인트 추가
    @GetMapping("/health")
    public String healthCheck() {
        log.info("Elasticsearch 헬스체크 요청");
        return "✅ Elasticsearch Controller 정상 작동중!";
    }
}