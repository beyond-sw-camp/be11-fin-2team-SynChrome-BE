package com.Synchrome.collabcontent.livechat.dtos;

import com.google.errorprone.annotations.NoAllocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindSessionIdDto {
    private Long channelId;
}
