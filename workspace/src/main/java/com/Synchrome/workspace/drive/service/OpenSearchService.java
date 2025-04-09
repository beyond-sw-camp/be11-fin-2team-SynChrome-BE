package com.Synchrome.workspace.drive.service;


import com.Synchrome.workspace.drive.domain.document;
import com.Synchrome.workspace.drive.domain.tag;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenSearchService {
    private final RestHighLevelClient client;

    public OpenSearchService(RestHighLevelClient client) {
        this.client = client;
    }

    public void indexDocument(document doc) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", doc.getId());
        data.put("title", doc.getTitle());
        data.put("content", doc.getContent());
        data.put("tags", doc.getTags().stream().map(tag::getName).toList());
        data.put("createdAt", doc.getCreatedAt());

        IndexRequest request = new IndexRequest("documents")
                .id(doc.getId().toString())
                .source(data);

        client.index(request, RequestOptions.DEFAULT);
    }
}