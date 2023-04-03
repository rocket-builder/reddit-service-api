package com.anthill.ofhelperredditmvc.services.parsers;

import java.util.regex.Pattern;

public abstract class AbstractStringParser<T> {

    protected abstract Pattern getPattern();

    public abstract T parse(String value);

    public boolean isCorrect(String value) {
        return getPattern().matcher(value).matches();
    }
}
