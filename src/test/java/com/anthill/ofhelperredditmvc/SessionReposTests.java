package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.PostingGroupRepos;
import com.anthill.ofhelperredditmvc.repos.SessionRepos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

@SpringBootTest
public class SessionReposTests {

    @Autowired
    SessionRepos sessionRepos;

    @Autowired
    PostingGroupRepos groupRepos;

    @Test
    void deleteSessionsByGroup_whenAllCorrect_thenDelete() throws ResourceNotFoundedException {
        //arrange
        var group = groupRepos.findById(121939L)
                .orElseThrow(ResourceNotFoundedException::new);

        //act
        sessionRepos.deleteAllByGroup_Id(group.getId());
        var sessions = sessionRepos.findAllByGroup(group);

        //assert
        assertTrue(sessions.isEmpty());
    }
}
