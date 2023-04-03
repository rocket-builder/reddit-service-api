package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.services.redditprofile.RedditAccountProfileGoogleSheetsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class RedditAccountProfileGoogleSheetsServiceTests {

    @Autowired
    private RedditAccountProfileGoogleSheetsService sheetsService;

    @Test
    void parseAccounts_whenAllCorrect_thenParse() throws IOException {
        var url = "https://docs.google.com/spreadsheets/d/1figY5DQ0AiWcnGr6TPwTNW6FtEKxdxbMF7DIPMXYyjk/edit#gid=609555091";

        var profiles = sheetsService.getFromUrl(url);

        assert profiles.size() == 2500;
    }
}
