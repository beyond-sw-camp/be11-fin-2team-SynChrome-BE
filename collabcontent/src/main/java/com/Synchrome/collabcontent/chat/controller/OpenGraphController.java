package com.Synchrome.collabcontent.chat.controller;

import com.Synchrome.collabcontent.chat.service.OpenGraphService;
import com.Synchrome.collabcontent.chat.service.OpenGraphService.OpenGraphData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/og")
@RequiredArgsConstructor
public class OpenGraphController {

    private final OpenGraphService ogService;

    @GetMapping("/preview")
    public OpenGraphData preview(@RequestParam String url) throws IOException {
        return ogService.fetchOpenGraph(url);
    }
}