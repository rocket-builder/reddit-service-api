package com.anthill.ofhelperredditmvc.services.parsers;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RedditPostIdUrlParser extends AbstractStringParser<Long> {

    @Override
    protected Pattern getPattern() {
        return Pattern.compile("^https:\\/\\/(www\\.)?reddit.(\\w{1,3})\\/r\\/(\\w+)\\/comments\\/(\\w+\\/)+$");
    }

    @Override
    public Long parse(String value) {
        //todo implement method

        return 0L;
    }
}
