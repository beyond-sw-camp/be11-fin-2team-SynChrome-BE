package com.Synchrome.workspace.drive.dto;


import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMetaData {
    private String fileName;
    private String uploader;
    private String extension;
    private String uploadDate; // ISO 포맷 문자열

    // Getter/Setter
}