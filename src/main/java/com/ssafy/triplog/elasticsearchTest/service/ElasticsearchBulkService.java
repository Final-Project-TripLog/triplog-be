package com.ssafy.triplog.elasticsearchTest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.triplog.elasticsearchTest.dto.PlanPostCacheIndexedDto;
import com.ssafy.triplog.elasticsearchTest.mapper.ElasticsearchTestMapper;
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
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchBulkService {

    private final RestHighLevelClient elasticsearchClient;
    private final ElasticsearchTestMapper elasticsearchTestMapper;
    private final ObjectMapper objectMapper;

    private static final String INDEX_NAME = "plan_posts";
    private static final int BATCH_SIZE = 1000; // 배치 크기

    /**
     * MySQL의 모든 plan_post 데이터를 Elasticsearch에 색인
     */
    public int indexAllPlanPosts() throws IOException {
        log.info("📊 MySQL에서 전체 데이터 조회 시작");

        // 1. MySQL에서 모든 데이터 조회
        List<PlanPostCacheIndexedDto> allPosts = elasticsearchTestMapper.searchByCacheIndexedOptimized("");
        log.info("📊 MySQL 조회 완료: {}개 문서", allPosts.size());

        if (allPosts.isEmpty()) {
            log.warn("⚠️ 색인할 데이터가 없습니다");
            return 0;
        }

        // 2. 인덱스 존재 확인 및 생성
        createIndexIfNotExists();

        // 3. 배치 단위로 색인
        int totalIndexed = 0;
        for (int i = 0; i < allPosts.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, allPosts.size());
            List<PlanPostCacheIndexedDto> batch = allPosts.subList(i, endIndex);

            int batchIndexed = indexBatch(batch, i / BATCH_SIZE + 1);
            totalIndexed += batchIndexed;

            log.info("📦 배치 {}/{} 완료: {}개 색인 (총 {}개)",
                    i / BATCH_SIZE + 1,
                    (allPosts.size() + BATCH_SIZE - 1) / BATCH_SIZE,
                    batchIndexed,
                    totalIndexed);
        }

        log.info("🎉 전체 색인 완료: {}개 문서", totalIndexed);
        return totalIndexed;
    }

    /**
     * 배치 단위로 문서 색인
     */
    private int indexBatch(List<PlanPostCacheIndexedDto> batch, int batchNumber) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (PlanPostCacheIndexedDto post : batch) {
            // DTO를 Map으로 변환
            Map<String, Object> documentMap = convertToMap(post);

            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(post.getNo().toString())
                    .source(documentMap, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        // Bulk 실행
        BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        if (bulkResponse.hasFailures()) {
            log.error("💥 배치 {} 색인 중 일부 실패: {}", batchNumber, bulkResponse.buildFailureMessage());
        }

        // 성공한 문서 수 반환
        return (int) java.util.stream.IntStream.range(0, bulkResponse.getItems().length)
                .filter(i -> !bulkResponse.getItems()[i].isFailed())
                .count();
    }

    /**
     * DTO를 Elasticsearch 문서용 Map으로 변환
     */
    private Map<String, Object> convertToMap(PlanPostCacheIndexedDto post) {
        Map<String, Object> map = new HashMap<>();

        map.put("no", post.getNo());
        map.put("user_nickname", post.getUserNickname());
        map.put("title", post.getTitle());
        map.put("description", post.getDescription());
        map.put("created_at", post.getCreatedAt());
        map.put("updated_at", post.getUpdatedAt());
        map.put("user_no", post.getUserNo());
        map.put("fork_count", post.getForkCount());
        map.put("view_count", post.getViewCount());
        map.put("liked_count", post.getLikedCount());
        map.put("thumbnail", post.getThumbnail());
        map.put("start_day", post.getStartDay());
        map.put("end_day", post.getEndDay());
        map.put("total_member", post.getTotalMember());
        map.put("tags", post.getTags());
        map.put("attraction_titles", post.getAttractionTitles());
        map.put("addresses", post.getAddresses());
        map.put("search_all", post.getSearchAll());

        return map;
    }

    /**
     * 인덱스가 없으면 생성
     */
    private void createIndexIfNotExists() throws IOException {
        GetIndexRequest getRequest = new GetIndexRequest(INDEX_NAME);
        boolean exists = elasticsearchClient.indices().exists(getRequest, RequestOptions.DEFAULT);

        if (!exists) {
            log.info("📝 Elasticsearch 인덱스 생성: {}", INDEX_NAME);

            CreateIndexRequest createRequest = new CreateIndexRequest(INDEX_NAME);

            // 한국어 분석기 설정
            String settings = """
                {
                    "settings": {
                        "number_of_shards": 1,
                        "number_of_replicas": 0,
                        "analysis": {
                            "analyzer": {
                                "korean": {
                                    "type": "standard"
                                }
                            }
                        }
                    },
                    "mappings": {
                        "properties": {
                            "no": { "type": "long" },
                            "user_nickname": { "type": "text", "analyzer": "korean" },
                            "title": { "type": "text", "analyzer": "korean" },
                            "description": { "type": "text", "analyzer": "korean" },
                            "tags": { "type": "text", "analyzer": "korean" },
                            "attraction_titles": { "type": "text", "analyzer": "korean" },
                            "addresses": { "type": "text", "analyzer": "korean" },
                            "search_all": { "type": "text", "analyzer": "korean" },
                            "created_at": { "type": "date" },
                            "updated_at": { "type": "date" },
                            "start_day": { "type": "date" },
                            "end_day": { "type": "date" },
                            "user_no": { "type": "long" },
                            "fork_count": { "type": "integer" },
                            "view_count": { "type": "integer" },
                            "liked_count": { "type": "integer" },
                            "total_member": { "type": "integer" },
                            "thumbnail": { "type": "keyword" }
                        }
                    }
                }
                """;

            createRequest.source(settings, XContentType.JSON);
            elasticsearchClient.indices().create(createRequest, RequestOptions.DEFAULT);

            log.info("✅ 인덱스 생성 완료: {}", INDEX_NAME);
        } else {
            log.info("📋 인덱스 이미 존재: {}", INDEX_NAME);
        }
    }

    /**
     * 인덱스 삭제
     */
    public void deleteIndex() throws IOException {
        GetIndexRequest getRequest = new GetIndexRequest(INDEX_NAME);
        boolean exists = elasticsearchClient.indices().exists(getRequest, RequestOptions.DEFAULT);

        if (exists) {
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(INDEX_NAME);
            elasticsearchClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("🗑️ 인덱스 삭제 완료: {}", INDEX_NAME);
        } else {
            log.info("📋 삭제할 인덱스가 없음: {}", INDEX_NAME);
        }
    }

    /**
     * Elasticsearch 연결 상태 확인
     */
    public boolean checkHealth() {
        try {
            return elasticsearchClient.ping(RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("💥 Elasticsearch 연결 확인 실패", e);
            return false;
        }
    }
}