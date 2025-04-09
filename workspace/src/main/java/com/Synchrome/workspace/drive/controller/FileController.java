package com.Synchrome.workspace.drive.controller;

import com.Synchrome.workspace.drive.domain.document;
import com.Synchrome.workspace.drive.repository.documentRepository;
import com.Synchrome.workspace.drive.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final documentRepository repository;

    public FileController(FileService fileService, documentRepository repository) {
        this.fileService = fileService;
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<document> getAllDocuments() {
        return repository.findAll();
    }
    @GetMapping("/tag")
    public List<document> getByTag(@RequestParam String name) {
        return repository.findByTags_Name(name);
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("authorId") String authorId,
            @RequestParam("tags") String tagString
    ) throws IOException {
        List<String> tags = Arrays.stream(tagString.split(","))
                .map(String::trim)
                .toList();

        fileService.saveFile(file, title, content, authorId, tags);
        return ResponseEntity.ok("File uploaded and indexed!");
    }
}
