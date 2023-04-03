package com.anthill.ofhelperredditmvc.services.parsers;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class TelegramTagParser extends AbstractStringParser<String> {

    @Override
    protected Pattern getPattern() {
        return Pattern.compile("^@\\w{4,20}$");
    }

    @Override
    public String parse(String value) {
        return getPattern().matcher(value).group();
    }
}
