package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.services.parsers.RedditPostIdUrlParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedditPostIdUrlParserTest {

    @Autowired
    RedditPostIdUrlParser urlParser;

    @Test
    void isCorrectUrl_whenCorrect_thenTrue() {
        var url = "https://www.reddit.com/r/adorableporn/comments/11dru2i/so_soft_and_adorable_21yo_beaty/";

        var isCorrect = urlParser.isCorrect(url);

        assertTrue(isCorrect);
    }

    @Test
    void isCorrectUrl_whenIncorrect_thenTrue() {
        var url = "https://www.reddit.com/r/adorableporn/comddments/11dru2i/so_soft_and_adorable_21yo_beaty/";

        var isCorrect = urlParser.isCorrect(url);

        assertFalse(isCorrect);
    }
}