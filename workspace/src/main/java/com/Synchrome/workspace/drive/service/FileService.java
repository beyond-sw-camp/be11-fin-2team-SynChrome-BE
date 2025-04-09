package com.Synchrome.workspace.drive.service;

import com.Synchrome.workspace.drive.common.S3Uploader;
import com.Synchrome.workspace.drive.domain.document;
import com.Synchrome.workspace.drive.domain.tag;
import com.Synchrome.workspace.drive.repository.documentRepository;
import com.Synchrome.workspace.drive.repository.tagRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final documentRepository docRepo;
    private final tagRepository tagRepo;
    private final S3Uploader s3Uploader;
    private final OpenSearchService osService;

    public FileService(documentRepository docRepo, tagRepository tagRepo,
                       S3Uploader s3Uploader, OpenSearchService osService) {
        this.docRepo = docRepo;
        this.tagRepo = tagRepo;
        this.s3Uploader = s3Uploader;
        this.osService = osService;
    }

    public void saveFile(MultipartFile file, String title, String content, String authorId, List<String> tagNames) throws IOException {
        String url = null;
        String fileType = null;

        if (file != null && !file.isEmpty()) {
            url = s3Uploader.uploadFile(file);
            fileType = file.getContentType();
        }

        List<tag> tags = tagNames.stream()
                .map(name -> tagRepo.findByName(name).orElseGet(() -> tagRepo.save(new tag(name))))
                .toList();

        document doc = new document();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setAuthorId(authorId);
        doc.setFileUrl(url);        // ← null이어도 저장 가능
        doc.setFileType(fileType);  // ← null이어도 저장 가능
        doc.setTags(tags);

        docRepo.save(doc);
        osService.indexDocument(doc);
    }

}