package com.anthill.ofhelperredditmvc.services.parsers;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ProxyParser extends AbstractStringParser<String>{

    @Override
    protected Pattern getPattern() {
        return Pattern.compile("^(http(s)?://)?(\\w+):(.+)@(.+):\\d{1,5}$");
    }

    @Override
    public String parse(String value) {
        return getPattern().matcher(value).toMatchResult().toString();
    }
}
