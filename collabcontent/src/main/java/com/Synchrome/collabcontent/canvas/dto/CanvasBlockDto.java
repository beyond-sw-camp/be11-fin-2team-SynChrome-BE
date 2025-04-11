package com.Synchrome.collabcontent.canvas.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class CanvasBlockDto {
    private String type;
    private String orderKey;
    private int indent;
    private JsonNode content; // tiptap JSON content
}
