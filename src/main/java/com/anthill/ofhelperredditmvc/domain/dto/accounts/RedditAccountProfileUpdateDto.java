package com.anthill.ofhelperredditmvc.domain.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RedditAccountProfileUpdateDto {

    private List<Long> ids;
    private RedditAccountProfileDto update;
}
