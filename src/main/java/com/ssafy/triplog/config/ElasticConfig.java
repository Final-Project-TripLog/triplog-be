package com.ssafy.triplog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ElasticConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.scheme}")
    private String scheme;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        log.info("Elasticsearch 연결 설정 - Host: {}, Port: {}, Scheme: {}", host, port, scheme);

        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(host, port, scheme))
                            .setRequestConfigCallback(requestConfigBuilder ->
                                    requestConfigBuilder
                                            .setConnectTimeout(5000)
                                            .setSocketTimeout(60000))
                            .setHttpClientConfigCallback(httpClientBuilder ->
                                    httpClientBuilder
                                            .setMaxConnTotal(100)
                                            .setMaxConnPerRoute(100))
            );

            log.info("✅ Elasticsearch 클라이언트 생성 완료");
            return client;
        } catch (Exception e) {
            log.error("❌ Elasticsearch 클라이언트 생성 실패", e);
            throw new RuntimeException("Elasticsearch 연결 설정 실패", e);
        }
    }
}