package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.RedditAccount;
import com.anthill.ofhelperredditmvc.domain.dto.bot.RedditAccountTokenDto;
import com.anthill.ofhelperredditmvc.domain.dto.bot.StatusDto;
import com.anthill.ofhelperredditmvc.domain.dto.bot.UpVoteGroupCreateDto;
import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.bot.UpVoteBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.exceptions.*;
import com.anthill.ofhelperredditmvc.services.group.actions.UpVoteGroupActionsService;
import com.anthill.ofhelperredditmvc.services.rest.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

import static java.util.Objects.nonNull;

@Tag(name = "Bot")
@RequestMapping("/bot")
@RestController
public class BotController {

    private final ModelMapper mapper;
    private final PostingGroupService postingGroupService;
    private final UpVoteGroupService upVoteGroupService;
    private final UpVoteGroupActionsService upVoteGroupActionsService;
    private final RedditAccountService accountService;
    private final SessionService sessionService;
    private final UserService userService;

    public BotController(ModelMapper mapper,
                         PostingGroupService postingGroupService,
                         UpVoteGroupService upVoteGroupService,
                         UpVoteGroupActionsService upVoteGroupActionsService,
                         RedditAccountService accountService,
                         SessionService sessionService,
                         UserService userService) {
        this.mapper = mapper;
        this.postingGroupService = postingGroupService;
        this.upVoteGroupService = upVoteGroupService;
        this.upVoteGroupActionsService = upVoteGroupActionsService;
        this.accountService = accountService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping("/group/{id}/extraUpvotes")
    public ResponseEntity<List<UpVoteBotSession>> getExtraSessionsForUpVote(
            @PathVariable("id") long id, @RequestParam int count) throws ResourceNotFoundedException {

        var group = upVoteGroupService.findById(id);
        if(!group.getStatus().equals(WorkStatus.WORK)){
            throw new IncorrectInputDataException("Incorrect group status!");
        }

        var sessions = upVoteGroupService.createExtraSessionsForBot(group, count);

        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    @PostMapping("/postingGroup/session/{id}/upVoteGroup")
    public ResponseEntity<UpVoteGroup> createUpVoteGroupBySession(
            @PathVariable("id") long id, @RequestBody UpVoteGroupCreateDto createDto)
            throws ResourceNotFoundedException, ResourceAlreadyExists, RedditBotServiceException, GroupAlreadyStartedException {

        var group = mapper.map(createDto, UpVoteGroup.class);
        var user = userService.findBySessionId(id);
        var saved = upVoteGroupService.save(group, user);

        var started = upVoteGroupActionsService.start(saved);
        //todo send started to page

        return new ResponseEntity<>(started, HttpStatus.OK);
    }

    @PatchMapping("/redditAccount/{id}/ban")
    public ResponseEntity<RedditAccount> patchBanned(
            @PathVariable("id") long id, @RequestBody boolean isBanned) throws ResourceNotFoundedException {

        var account = accountService.findById(id);
        account.setBanned(isBanned);
        var updated = accountService.update(account);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/redditAccount/{id}/suspend")
    public ResponseEntity<RedditAccount> patchSuspend(
            @PathVariable("id") long id, @RequestBody Long timestamp) throws ResourceNotFoundedException {

        var account = accountService.findById(id);

        if(nonNull(timestamp)){
            account.setSuspendTime(new Date(timestamp));
        }
        account.setSuspend(true);

        var updated = accountService.update(account);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/redditAccount/{id}/token")
    public ResponseEntity<RedditAccount> putRedditAccountToken(
            @PathVariable("id") long id, @RequestBody RedditAccountTokenDto token)
            throws ResourceNotFoundedException {

        var account = accountService.findById(id);
        var updated = accountService.updateToken(account, token);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/redditAccount/{id}/disreputationPoints/add")
    public ResponseEntity<RedditAccount> addDisreputationPoint(@PathVariable("id") long id)
            throws ResourceNotFoundedException {
        var account = accountService.addDisreputationPointById(id);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PatchMapping("/redditAccount/{id}/disreputationPoints/reset")
    public ResponseEntity<RedditAccount> resetReputation(@PathVariable("id") long id)
            throws ResourceNotFoundedException {
        var account = accountService.resetReputationById(id);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PatchMapping("/redditAccount/{id}/passedUpVotes/add")
    public ResponseEntity<RedditAccount> addPassedUpVotes(@PathVariable("id") long id)
            throws ResourceNotFoundedException {
        var account = accountService.addPassedUpVoteById(id);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PatchMapping("/session/{id}/status")
    public ResponseEntity<Session> updateSessionStatus(@PathVariable("id") long id, @RequestBody StatusDto status)
            throws ResourceNotFoundedException {

        var session = sessionService.findById(id);
        var updated = sessionService.updateStatus(session, status);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/upVoteGroup/{id}/status")
    public ResponseEntity<UpVoteGroup> updateUpVoteGroupStatus(@PathVariable("id") long id, @RequestBody StatusDto status)
            throws ResourceNotFoundedException {
        var group = upVoteGroupService.findById(id);

        var update = upVoteGroupService.updateStatus(group, status);

        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    @PatchMapping("/upVoteGroup/{id}/refund")
    public ResponseEntity<String> upVoteGroupRefund(@PathVariable("id") long id, @RequestParam int upVotesCount)
            throws ResourceNotFoundedException {
        var group = upVoteGroupService.findById(id);

        userService.refundUpVotes(group.getUser(), upVotesCount);

        return new ResponseEntity<>("Success!", HttpStatus.OK);
    }

    @PatchMapping("/postingGroup/{id}/status")
    public ResponseEntity<PostingGroup> updatePostingGroupStatus(@PathVariable("id") long id, @RequestBody StatusDto status)
            throws ResourceNotFoundedException {

        var group = postingGroupService.findById(id);
        var update = postingGroupService.updateStatus(group, status);

        return new ResponseEntity<>(update, HttpStatus.OK);
    }
}
