package com.anthill.ofhelperredditmvc.domain;

import lombok.Getter;

@Getter
public enum RedditAccountFilter {

    ALL("all"),
    BANNED("banned"), NOT_BANNED("not_banned"),
    SUSPEND("suspend"), NOT_SUSPEND("non_suspend");

    private final String value;
    RedditAccountFilter(String value) {
        this.value = value;
    }
}
