package com.Synchrome.collabcontent.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccessTokendto {
    private String access_token;
    private String expires_in;
    private String scope;
    private String id_token;
}
