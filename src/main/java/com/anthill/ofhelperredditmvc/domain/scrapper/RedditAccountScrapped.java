package com.anthill.ofhelperredditmvc.domain.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedditAccountScrapped {

    @JsonProperty("name")
    private String username;

    @JsonProperty("total_karma")
    private int karma;

    @JsonProperty("is_suspended")
    private boolean isSuspend;

    private boolean isBanned;
}
