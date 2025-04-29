package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkSpaceUpdateDto {
    @NotNull
    private Long workSpaceId;
    @NotNull
    private Long userId;
    private Long changeMasterId;
    private String title;
    private MultipartFile logo;
}
