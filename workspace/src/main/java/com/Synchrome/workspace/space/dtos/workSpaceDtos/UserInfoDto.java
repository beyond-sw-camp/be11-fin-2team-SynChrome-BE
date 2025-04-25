package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import com.Synchrome.workspace.space.domain.ENUM.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInfoDto {
    private Long id;
    private String name;
    private String email;
    private Subscribe subscribe;
    private String profile;
}
