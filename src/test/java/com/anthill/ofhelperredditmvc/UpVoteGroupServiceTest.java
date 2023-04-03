package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.services.rest.UpVoteGroupService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UpVoteGroupServiceTest {

    @Autowired
    UpVoteGroupService groupService;

    @Autowired
    UserService userService;

    @Test
    void createUpVoteGroup_whenAllCorrect_thenCreate() throws ResourceAlreadyExists {
        var user = userService.findByLogin("liker");

        var group = UpVoteGroup.builder()
                .postUrl("https://www.reddit.com/r/adorableporn/comments/11dru2i/so_soft_and_adorable_21yo_beaty/")
                .upVoteCount(100)
                .build();

        var saved = groupService.save(group, user);

        assertTrue(saved.getId() > 0);
    }
}