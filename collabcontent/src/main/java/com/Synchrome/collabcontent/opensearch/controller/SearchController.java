package com.Synchrome.collabcontent.opensearch.controller;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final RestHighLevelClient client;

    public SearchController(RestHighLevelClient client) {
        this.client = client;
    }

    @GetMapping
    public List<Map<String, Object>> search(@RequestParam String keyword) throws IOException {
        SearchRequest request = new SearchRequest("documents");

        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.multiMatchQuery(keyword, "title", "content", "tags"));

        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        return Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .toList();
    }

    @GetMapping("/keyword")
    public List<Map<String, Object>> searchMessages(@RequestParam String keyword) throws IOException {
        SearchRequest request = new SearchRequest("chat_messages");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.queryStringQuery("*" + keyword + "*").field("content"));
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            result.add(hit.getSourceAsMap());
        }
        return result;
    }
}