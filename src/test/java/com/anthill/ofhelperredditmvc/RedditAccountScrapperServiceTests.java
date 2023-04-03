package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.exceptions.RedditAccountScrapperException;
import com.anthill.ofhelperredditmvc.services.RedditAccountScrapperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@SpringBootTest
public class RedditAccountScrapperServiceTests {

    @Autowired
    RedditAccountScrapperService scrapperService;

    @Test
    void scrapRedditAccount_whenAccountSuspend_thenScrap() throws IOException, InterruptedException {
        var username = "Fair-Manner-4211";

        var account = scrapperService.scrap(username);

        assert account.isSuspend();
    }

    @Test
    void scrapRedditAccount_whenAccountHasKarma_thenScrap() throws IOException, InterruptedException {
        var username = "GunnCelt";

        var account = scrapperService.scrap(username);

        assert account.getKarma() > 0;
    }

    @Test
    void scrapRedditAccount_whenAccountBanned_thenScrap() throws IOException, InterruptedException {
        var username = "hoekstut3KhM1";

        var account = scrapperService.scrap(username);

        assert account.isBanned();
    }

    @Test
    void scrapWithRetryRedditAccount_whenError_thenThrowError(){
        var username = "https://old.reddit.com";
        var tryCount = 5;

        assertThrows(RedditAccountScrapperException.class, () -> scrapperService.scrapWithRetry(username, tryCount));
    }

    @Test
    void scrapWithRetryRedditAccount_whenAllCorrect_thenThrowError(){
        var username = "GunnCelt";
        var tryCount = 5;

        var scrapped = scrapperService.scrapWithRetry(username, tryCount);

        assert scrapped.getKarma() > 0;
    }
}
