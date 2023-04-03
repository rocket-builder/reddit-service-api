package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.exceptions.*;
import com.anthill.ofhelperredditmvc.repos.SessionRepos;
import com.anthill.ofhelperredditmvc.services.rest.PostingGroupService;
import com.anthill.ofhelperredditmvc.services.group.actions.PostingGroupActionsService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class ExcelPostingGroupActionsServiceTests {

    @Autowired
    PostingGroupService groupService;

    @Autowired
    UserService userService;

    @Autowired
    PostingGroupActionsService postingGroupActionsService;

    @Autowired
    SessionRepos sessionRepos;

    @Test
    void startGroup_whenAllCorrect_thenStart()
            throws RedditBotServiceException, ResourceNotFoundedException,
            GoogleSheetsAccessException, GoogleSheetsReadException,
            GroupAlreadyStartedException {
        //arrange
        var user = userService.findByLogin("user");

        var group = groupService.findById(122161L, user);
        group.setSessions(new TreeSet<>(sessionRepos.findAllByGroup(group)));

        //act
        group = postingGroupActionsService.start(group);

        //assert
        assertFalse(group.getSessions().isEmpty());
    }

    @Test
    void stopGroup_whenAllCorrect_thenStop()
            throws ResourceNotFoundedException, RedditBotServiceException,
                GroupAlreadyStoppedException, GoogleSheetsAccessException {
        //arrange
        var user = userService.findByLogin("user");
        var group = groupService.findById(51272, user);

        //act
        group = postingGroupActionsService.stop(group);

        //assert
        assertEquals(group.getStatus(), WorkStatus.DONE);
    }
}
