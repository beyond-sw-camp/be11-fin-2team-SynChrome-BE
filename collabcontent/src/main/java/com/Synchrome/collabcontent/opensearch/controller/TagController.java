package com.Synchrome.collabcontent.opensearch.controller;


import com.Synchrome.collabcontent.opensearch.repository.tagRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.Synchrome.collabcontent.opensearch.domain.tag;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final tagRepository tagRepo;

    public TagController(tagRepository tagRepo) {
        this.tagRepo = tagRepo;
    }

    @GetMapping
    public List<String> getAllTags() {
        return tagRepo.findAll()
                .stream()
                .map(tag::getName)
                .toList();
    }
    @GetMapping("/search")
    public List<String> suggestTags(@RequestParam String keyword) {
        return tagRepo.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(tag::getName)
                .toList();
    }
}