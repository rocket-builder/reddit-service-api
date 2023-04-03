package com.anthill.ofhelperredditmvc.domain;

import lombok.Getter;

@Getter
public enum RedditAccountProfileSearchFields {

    LOGIN("login"), PROXY("proxy");

    private final String value;

    RedditAccountProfileSearchFields(String value) {
        this.value = value;
    }
}
