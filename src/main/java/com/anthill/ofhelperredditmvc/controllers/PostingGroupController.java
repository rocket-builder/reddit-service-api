package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.session.bot.PostingBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.PostingBotGroup;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRestService;
import com.anthill.ofhelperredditmvc.services.group.actions.AbstractGroupActionsService;
import com.anthill.ofhelperredditmvc.services.security.JwtUserRetriever;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Posting Group")
@RequestMapping("/postingGroup")
@RestController
public class PostingGroupController extends AbstractGroupController<PostingGroup> {

    protected PostingGroupController(IAuthenticatedRestService<PostingGroup> rest,
                                     AbstractGroupActionsService<PostingGroup> actionService,
                                     JwtUserRetriever jwt) {
        super(actionService, jwt, rest);
    }
}
