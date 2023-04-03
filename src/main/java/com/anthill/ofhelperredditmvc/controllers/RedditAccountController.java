package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.RedditAccount;
import com.anthill.ofhelperredditmvc.interfaces.IRestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reddit Account")
@RequestMapping("/redditAccount")
@RestController
public class RedditAccountController extends AbstractRestController<RedditAccount> {

    protected RedditAccountController(IRestService<RedditAccount> rest) {
        super(rest);
    }
}
