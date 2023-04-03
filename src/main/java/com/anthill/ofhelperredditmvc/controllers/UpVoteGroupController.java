package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.session.bot.UpVoteBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.UpVoteBotGroup;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRestService;
import com.anthill.ofhelperredditmvc.services.group.actions.AbstractGroupActionsService;
import com.anthill.ofhelperredditmvc.services.security.JwtUserRetriever;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UpVote Group")
@RequestMapping("/upVoteGroup")
@RestController
public class UpVoteGroupController extends AbstractGroupController<UpVoteGroup>{


    protected UpVoteGroupController(AbstractGroupActionsService<UpVoteGroup> actionService,
                                    JwtUserRetriever jwt, IAuthenticatedRestService<UpVoteGroup> rest) {
        super(actionService, jwt, rest);
    }
}
