package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.services.rest.RedditAccountService;
import com.anthill.ofhelperredditmvc.services.rest.UseragentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

@SpringBootTest
public class RedditAccountServiceTests {

    @Autowired
    RedditAccountService accountService;

    @Autowired
    UseragentService useragentService;

    @Test
    void setUserAgentsToAccounts_whenAllCorrect_thenSet(){

        var accounts = accountService.findAll().stream()
                .filter(a -> a.getUseragent() == null)
                .collect(Collectors.toList());

        accounts = accounts.stream()
                .peek(a -> a.setUseragent(useragentService.findRandom().getValue()))
                .collect(Collectors.toList());

        var saved = accountService.saveAll(accounts);

        assert saved != null;
    }
}
