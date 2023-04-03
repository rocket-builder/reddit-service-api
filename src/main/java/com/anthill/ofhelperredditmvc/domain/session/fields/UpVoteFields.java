package com.anthill.ofhelperredditmvc.domain.session.fields;

import lombok.Getter;

@Getter
public enum UpVoteFields {
    POST_URL("A", 0), UPVOTE_COUNT("B", 1),

    ROTATE_PROXY("E", 4);

    private final String literal;
    private final int index;

    UpVoteFields(String literal, int index) {
        this.literal = literal;
        this.index = index;
    }

    public String getRange(int number){
        return literal + number;
    }
}
