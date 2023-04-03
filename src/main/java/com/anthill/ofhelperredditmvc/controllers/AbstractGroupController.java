package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.session.bot.AbstractBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.AbstractGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.AbstractBotGroup;
import com.anthill.ofhelperredditmvc.exceptions.*;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRestService;
import com.anthill.ofhelperredditmvc.services.group.actions.AbstractGroupActionsService;
import com.anthill.ofhelperredditmvc.services.security.JwtUserRetriever;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractGroupController<G extends AbstractGroup> extends AbstractAuthenticatedRestController<G> {

    private final AbstractGroupActionsService<G> actionService;

    protected AbstractGroupController(AbstractGroupActionsService<G> actionService,
                                      JwtUserRetriever jwt,
                                      IAuthenticatedRestService<G> rest) {
        super(rest, jwt);
        this.actionService = actionService;
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<G> startById(@PathVariable("id") long groupId, HttpServletRequest request)
            throws ResourceNotFoundedException,
            RedditBotServiceException, GoogleSheetsAccessException,
            GoogleSheetsReadException, GroupAlreadyStartedException {

        var user = jwt.getUserFromRequest(request);
        var group = rest.findById(groupId, user);

        var started = actionService.start(group);

        return new ResponseEntity<>(started, HttpStatus.OK);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<G> stopById(@PathVariable("id") long groupId, HttpServletRequest request)
            throws ResourceNotFoundedException,
            RedditBotServiceException, GroupAlreadyStoppedException {

        var user = jwt.getUserFromRequest(request);
        var group = rest.findById(groupId, user);

        var stopped = actionService.stop(group);

        return new ResponseEntity<>(stopped, HttpStatus.OK);
    }
}
