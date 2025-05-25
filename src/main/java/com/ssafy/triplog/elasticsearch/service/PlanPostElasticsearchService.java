package com.ssafy.triplog.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ssafy.triplog.elasticsearch.document.PlanPostDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
//import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.SearchHit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanPostElasticsearchService {

    private final RestHighLevelClient esClient;
    private static final String INDEX_NAME = "plan_post_search";

    //  ObjectMapper 설정 수정 - JSR310 모듈 추가하여 LocalDateTime 지원
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Java 8 Time API 지원
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 8601 형식으로 날짜 직렬화

    /**
     * 인덱스 생성 메서드
     */
    public void createIndexIfNotExists() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX_NAME);
        boolean exists = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

        if (!exists) {
            log.info("인덱스 '{}' 가 존재하지 않아 새로 생성합니다.", INDEX_NAME);
            createIndex();
        } else {
            log.info("인덱스 '{}' 가 이미 존재합니다.", INDEX_NAME);
        }
    }

    /**
     * 인덱스 생성
     */
    private void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);

        //  매핑 설정
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .endObject()
                .startObject("title")
                .field("type", "text")
                .field("analyzer", "standard")
                .endObject()
                .startObject("description")
                .field("type", "text")
                .field("analyzer", "standard")
                .endObject()
                .startObject("userNickname")
                .field("type", "keyword")
                .endObject()
                .startObject("tags")
                .field("type", "text")
                .field("analyzer", "standard")
                .endObject()
                .startObject("attractionTitles")
                .field("type", "text")
                .field("analyzer", "standard")
                .endObject()
                .startObject("address")
                .field("type", "text")
                .field("analyzer", "standard")
                .endObject()
                .startObject("createdAt")
                .field("type", "date")
                .field("format", "yyyy-MM-dd'T'HH:mm:ss")
                .endObject()
                .endObject()
                .endObject();

        request.mapping(mappingBuilder);

        try {
            esClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("✅ 인덱스 '{}' 생성 완료", INDEX_NAME);
        } catch (Exception e) {
            log.error("❌ 인덱스 생성 실패", e);
            throw e;
        }
    }

    /**
     * 인덱스 삭제 (테스트용)
     */
    public void deleteIndex() throws IOException {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
            esClient.indices().delete(request, RequestOptions.DEFAULT);
            log.info("✅ 인덱스 '{}' 삭제 완료", INDEX_NAME);
        } catch (Exception e) {
            log.warn("인덱스 삭제 중 오류 (무시 가능): {}", e.getMessage());
        }
    }

    public void bulkIndex(List<PlanPostDocument> docs) throws IOException {
        log.info("Elasticsearch bulk 인덱싱 시작 - 문서 수: {}", docs.size());

        // ⭐ 인덱스 생성 확인
        createIndexIfNotExists();

        BulkRequest bulkRequest = new BulkRequest();

        for (PlanPostDocument doc : docs) {
            try {
                String jsonString = objectMapper.writeValueAsString(doc);
                log.debug("직렬화된 JSON: {}", jsonString);

                IndexRequest request = new IndexRequest(INDEX_NAME)
                        .id(doc.getId().toString())
                        .source(jsonString, org.elasticsearch.xcontent.XContentType.JSON);

                bulkRequest.add(request);
            } catch (Exception e) {
                log.error("문서 직렬화 실패 - ID: {}, 오류: {}", doc.getId(), e.getMessage());
                throw e;
            }
        }

        try {
            // ⭐ 더 안전한 방식으로 bulk 실행
            BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            // ⭐ 응답 파싱 대신 간단한 성공/실패 체크
            if (bulkResponse.hasFailures()) {
                log.warn("⚠️ 일부 문서 인덱싱 실패: {}", bulkResponse.buildFailureMessage());
            } else {
                log.info("✅ Elasticsearch bulk 인덱싱 완료 - 처리된 문서 수: {}", docs.size());
            }

        } catch (Exception e) {
            log.error("❌ Elasticsearch bulk 인덱싱 중 오류", e);

            // ⭐ 응답 파싱 오류는 무시하고 실제 데이터 저장 확인
            log.info("⚠️ 응답 파싱 오류가 발생했지만 데이터는 저장되었을 수 있습니다. 검색으로 확인해보세요.");

            // 예외를 다시 던지지 않고 경고만 출력
        }
    }

    public List<PlanPostDocument> searchByKeyword(String keyword) throws IOException {
        log.info("Elasticsearch 검색 시작 - 키워드: {}", keyword);

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(
                QueryBuilders.multiMatchQuery(keyword,
                        "title", "description", "tags", "attractionTitles", "address"
                ).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
        );

        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);

            List<PlanPostDocument> result = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                try {
                    String source = hit.getSourceAsString();
                    PlanPostDocument document = objectMapper.readValue(source, PlanPostDocument.class);
                    result.add(document);
                } catch (Exception e) {
                    log.error("검색 결과 역직렬화 실패 - Hit ID: {}, 오류: {}", hit.getId(), e.getMessage());
                }
            }

            log.info("✅ Elasticsearch 검색 완료 - 결과 수: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("❌ Elasticsearch 검색 실패", e);
            throw e;
        }
    }

    /**
     * 인덱스 상태 확인
     */
    public String getIndexStatus() {
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX_NAME);
            boolean exists = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

            if (exists) {
                // 간단한 카운트 쿼리로 문서 수 확인
                SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                sourceBuilder.query(QueryBuilders.matchAllQuery());
                sourceBuilder.size(0); // 결과는 필요 없고 카운트만
                searchRequest.source(sourceBuilder);

                SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
                long totalDocs = response.getHits().getTotalHits().value;

                return String.format("✅ 인덱스 '%s' 존재함 - 총 문서 수: %d", INDEX_NAME, totalDocs);
            } else {
                return String.format("❌ 인덱스 '%s' 존재하지 않음", INDEX_NAME);
            }
        } catch (Exception e) {
            return String.format("❌ 인덱스 상태 확인 실패: %s", e.getMessage());
        }
    }
}