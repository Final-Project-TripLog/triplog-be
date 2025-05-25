package com.ssafy.triplog.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.triplog.elasticsearch.document.PlanPostDocument;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
public class PlanPostElasticsearchService {

    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void bulkIndex(List<PlanPostDocument> docs) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (PlanPostDocument doc : docs) {
            IndexRequest request = new IndexRequest("plan_post_search")
                    .id(doc.getId().toString())
                    .source(objectMapper.writeValueAsString(doc), XContentType.JSON);

            bulkRequest.add(request);
        }

        esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    public List<PlanPostDocument> searchByKeyword(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest("plan_post_search");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(
                QueryBuilders.multiMatchQuery(keyword,
                        "title", "description", "tags", "attractionTitles", "address"
                ).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
        );

        searchRequest.source(sourceBuilder);

        SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);

        List<PlanPostDocument> result = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            String source = hit.getSourceAsString();
            result.add(objectMapper.readValue(source, PlanPostDocument.class));
        }

        return result;
    }
}
