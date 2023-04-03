package com.anthill.ofhelperredditmvc.domain.session.fields;

import lombok.Getter;

@Getter
public enum StatusFields {
    STATUS("H", 7), MESSAGE("I", 8);

    private final String literal;
    private final int index;

    StatusFields(String literal, int index) {
        this.literal = literal;
        this.index = index;
    }

    public String getRange(int rowNumber){
        return literal + rowNumber;
    }
}
