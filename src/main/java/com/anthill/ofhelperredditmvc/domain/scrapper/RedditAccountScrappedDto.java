package com.anthill.ofhelperredditmvc.domain.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedditAccountScrappedDto {

    @JsonProperty("data")
    private RedditAccountScrapped redditAccount;
}
