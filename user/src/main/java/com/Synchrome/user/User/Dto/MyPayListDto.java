package com.Synchrome.user.User.Dto;

import com.Synchrome.user.User.Domain.Enum.Paystatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MyPayListDto {
    private Paystatus paystatus;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
