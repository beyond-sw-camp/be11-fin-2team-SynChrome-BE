package com.Synchrome.collabcontent.chat.service;


import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenGraphService {

    public OpenGraphData fetchOpenGraph(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .timeout(3000)
                .userAgent("Mozilla")
                .get();

        OpenGraphData data = new OpenGraphData();
        data.setUrl(url);

        Element title = doc.selectFirst("meta[property=og:title]");
        Element desc = doc.selectFirst("meta[property=og:description]");
        Element image = doc.selectFirst("meta[property=og:image]");

        if (title != null) data.setTitle(title.attr("content"));
        if (desc != null) data.setDescription(desc.attr("content"));
        if (image != null) data.setImage(image.attr("content"));

        return data;
    }

    @Data
    public static class OpenGraphData {
        private String url;
        private String title;
        private String description;
        private String image;
    }
}