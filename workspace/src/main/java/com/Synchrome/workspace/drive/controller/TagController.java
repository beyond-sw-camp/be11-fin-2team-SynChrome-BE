package com.Synchrome.workspace.drive.controller;


import com.Synchrome.workspace.drive.domain.tag;
import com.Synchrome.workspace.drive.repository.tagRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                .map(t -> t.getName())
                .toList();
    }
    @GetMapping("/search")
    public List<String> suggestTags(@RequestParam String keyword) {
        return tagRepo.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(t -> t.getName())
                .toList();
    }
}