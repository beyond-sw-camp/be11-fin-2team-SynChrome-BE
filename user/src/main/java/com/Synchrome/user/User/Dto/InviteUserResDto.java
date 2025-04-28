package com.Synchrome.user.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class InviteUserResDto {
    private List<Long> userId;
}
