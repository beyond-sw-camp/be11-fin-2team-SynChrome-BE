package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WorkSpaceCreateDto {
    private String title;
    private Long userId;
    private MultipartFile logo;
}
