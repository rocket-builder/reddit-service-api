package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.domain.*;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.services.rest.RedditAccountProfileService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
public class RedditAccountProfileServiceTests {

    @Autowired
    private RedditAccountProfileService profileService;

    @Autowired
    private UserService userService;

    @Test
    void createProfile_whenAllDataNew_thenCreate() throws ResourceAlreadyExists, ResourceNotFoundedException {
        //arrange
        var userId = 6;
        var user = userService.findById(userId);
        var redditAccount = new RedditAccount("reddit", "reddit3");
        var proxy = new Proxy("http://127.0.0.1:2000@login:password2");
        var access = AccountAccess.ALL;

        var profile = RedditAccountProfile.builder()
                .redditAccount(redditAccount)
                .proxy(proxy)
                .access(access)
                .build();

        //act
        var saved = profileService.save(profile, user);

        //assert
        assert saved.getId() > 0;
    }

    @Test
    void createManyProfiles_whenAllCorrect_thenCreate() throws ResourceNotFoundedException {
        long userId = 6;
        var user = userService.findById(userId);

        var accounts = IntStream.range(0, 2500)
                .mapToObj(number -> new RedditAccount("reddit" + number, "reddit" + number))
                .collect(Collectors.toList());

        var proxy = new Proxy("http://127.0.0.1:2000@login:password2");
        var access = AccountAccess.ALL;

        List<RedditAccountProfile> profiles = accounts.stream()
                .map(account -> RedditAccountProfile.builder()
                    .redditAccount(account)
                    .proxy(proxy)
                    .access(access)
                    .build()
                )
                .collect(Collectors.toList());

        profileService.saveAll(profiles, user);
        assert true;
    }
}
