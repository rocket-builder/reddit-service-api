package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService userService;


    @Test
    void withdrawUpVotes_whenAllCorrect_thenWithDraw(){
        var user = userService.findByLogin("liker");
        var upVotes = 100;
        var balance = user.getUpVoteBalance() - upVotes;

        userService.withdrawUpVotes(user, upVotes);

        var withdrawUser = userService.findByLogin("liker");

        assertEquals(balance, withdrawUser.getUpVoteBalance());
    }
}
