package com.Synchrome.user.User.Dto;

import com.Synchrome.user.User.Domain.Enum.Subscribe;
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
