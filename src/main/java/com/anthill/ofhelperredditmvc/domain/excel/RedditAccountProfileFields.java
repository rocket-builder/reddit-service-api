package com.anthill.ofhelperredditmvc.domain.excel;

import lombok.Getter;

@Getter
public enum RedditAccountProfileFields {
    REDDIT_ACCOUNT(0), PROXY(1), ACCESS_TYPE(7), IS_SHARED(8);

    private final int index;

    RedditAccountProfileFields(int i) {
        this.index = i;
    }
}
