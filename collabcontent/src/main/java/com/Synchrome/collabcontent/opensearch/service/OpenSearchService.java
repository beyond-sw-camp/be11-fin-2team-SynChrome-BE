package com.Synchrome.collabcontent.opensearch.service;

import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.opensearch.domain.document;
import com.Synchrome.collabcontent.opensearch.domain.tag;
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

    public void indexChatMessage(ChatMessage message) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", message.getId());
        data.put("userId", message.getUserId());
        data.put("roomId", message.getChatRoom().getId());
        data.put("content", message.getContent());
        data.put("createdAt", message.getCreatedTime());
        data.put("workspaceId", message.getWorkspaceId());
        data.put("workspaceTitle", message.getWorkspaceTitle());

        System.out.println("✅ OpenSearch 인덱싱: " + data); // 로그 추가

        IndexRequest request = new IndexRequest("chat_messages") // ✅ chat index
                .id(message.getId().toString())                      // ID 충돌 방지
                .source(data);

        client.index(request, RequestOptions.DEFAULT);
    }
}