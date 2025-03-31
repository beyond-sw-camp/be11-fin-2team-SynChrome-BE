package com.Synchrome.collabcontent.canvas.dto;

import lombok.Data;

@Data
public class DocumentMessageDto {
    private String documentId;
    private String userId;
    private String update; // base64 encoded Yjs update
}