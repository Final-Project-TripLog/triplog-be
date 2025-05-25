package com.ssafy.triplog.attraction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.triplog.attraction.document.AttractionDocument;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionElasticsearchService {

    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void save(AttractionDocument doc) throws IOException {
        IndexRequest request = new IndexRequest("attractions"); // ES 인덱스 이름
        request.id(doc.getId().toString()); // 문서 ID
        request.source(objectMapper.writeValueAsString(doc), XContentType.JSON); // JSON으로 변환
        esClient.index(request, RequestOptions.DEFAULT); // Elasticsearch에 저장
    }

    public List<AttractionDocument> search(String keyword) throws IOException {
        SearchRequest request = new SearchRequest("attractions");

        // title 또는 region에 keyword 포함된 문서 검색
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "region"));

        request.source(sourceBuilder);

        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        List<AttractionDocument> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            String json = hit.getSourceAsString();
            AttractionDocument doc = objectMapper.readValue(json, AttractionDocument.class);
            results.add(doc);
        }

        return results;
    }

}
