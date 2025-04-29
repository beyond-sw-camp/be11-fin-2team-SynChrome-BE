package com.Synchrome.workspace.space.dtos.channelDtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelUpdateDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long channelId;
    private String title;
    private Long sectionId;
}
