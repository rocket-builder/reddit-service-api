package com.anthill.ofhelperredditmvc.services.parsers;

import com.anthill.ofhelperredditmvc.exceptions.IncorrectGoogleSheetUrlException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class GoogleSheetIdUrlParser extends AbstractStringParser<String> {

    @Override
    protected Pattern getPattern() {
        return Pattern.compile("https:\\/\\/docs\\.google\\.com\\/spreadsheets\\/d\\/(.+)\\/(.+)");
    }

    @Override
    public String parse(String url){
        var matcher = getPattern().matcher(url);

        if(matcher.matches() && matcher.toMatchResult().groupCount() >= 2) {
            return matcher.toMatchResult().group(1);
        }

        throw new IncorrectGoogleSheetUrlException();
    }

    @Override
    public boolean isCorrect(String url) {
        var matcher = getPattern().matcher(url);

        return matcher.matches() && matcher.toMatchResult().groupCount() >= 2;
    }
}
