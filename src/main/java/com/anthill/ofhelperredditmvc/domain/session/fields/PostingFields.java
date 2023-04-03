package com.anthill.ofhelperredditmvc.domain.session.fields;

import lombok.Getter;

@Getter
public enum PostingFields {

    REDDIT_ACCOUNT("A", 0), SUB_REDDIT("B", 1), TITLE("C", 2),
    IMAGE_URL("D", 3), COMMENT("E", 4), FLAIRS("F", 5),
    UP_VOTE_COUNT("G", 6),

    PROXY("J", 9);

    private final String literal;
    private final int index;

    PostingFields(String literal, int i) {
        this.literal = literal;
        this.index = i;
    }

    public String getRange(int number){
        return literal + number;
    }
}
