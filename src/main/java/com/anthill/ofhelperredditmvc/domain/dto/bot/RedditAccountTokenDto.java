package com.anthill.ofhelperredditmvc.domain.dto.bot;

import lombok.Data;

@Data
public class RedditAccountTokenDto {

    private String accessToken, refreshToken;
}
