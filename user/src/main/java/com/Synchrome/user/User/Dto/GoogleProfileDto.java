package com.Synchrome.user.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class GoogleProfileDto {
    private String sub;
    private String email;
    private String name;
    private String picture;
}
