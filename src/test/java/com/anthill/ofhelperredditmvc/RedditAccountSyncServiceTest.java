package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.services.RedditAccountSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedditAccountSyncServiceTest {

    @Autowired
    RedditAccountSyncService accountSyncService;

    @Test
    void syncAccountsWithReddit_whenAllCorrect_thenSync() {

        assertDoesNotThrow(() -> accountSyncService.syncAccountsWithReddit());
    }
}