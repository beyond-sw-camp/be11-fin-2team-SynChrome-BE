package com.Synchrome.user.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateProfileDto {
    private Long userId;
    private MultipartFile newProfile;
}
