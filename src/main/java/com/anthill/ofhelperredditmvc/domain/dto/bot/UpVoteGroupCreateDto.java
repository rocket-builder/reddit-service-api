package com.anthill.ofhelperredditmvc.domain.dto.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpVoteGroupCreateDto {

    private String postUrl;
    private int upVoteCount;
}
