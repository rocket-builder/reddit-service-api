package com.anthill.ofhelperredditmvc.domain.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RedditAccountProfileDto {

    private String login, password, proxy, access;
    private boolean isShared;
}
