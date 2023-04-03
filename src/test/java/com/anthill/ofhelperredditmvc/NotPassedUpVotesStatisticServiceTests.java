package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.repos.UpVoteGroupRepos;
import com.anthill.ofhelperredditmvc.repos.UserRepos;
import com.anthill.ofhelperredditmvc.services.statistic.NotPassedUpVotesStatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotPassedUpVotesStatisticServiceTests {

    @Autowired
    NotPassedUpVotesStatisticService statisticService;

    @Autowired
    UserRepos userRepos;

    @Autowired
    RedditAccountProfileRepos profileRepos;

    @Autowired
    UpVoteGroupRepos groupRepos;

    @Test
    void takeStatistic_whenAllCorrect_shouldTake() throws ResourceNotFoundedException {
//        var user = userRepos.findById(22832L)
//                .orElseThrow(ResourceNotFoundedException::new);
//
//        var profiles = profileRepos.findRandomSharedProfilesForUpVote(3);
//
//        var group = UpVoteGroup.builder()
//                        .postUrl("https://www.reddit.com/r/adorableporn/comments/11dru2i/so_soft_and_adorable_21yo_beaty/")
//                        .name("Order_Synthetic_" + new Date().getTime())
//                        .user(user)
//                        .upVoteCount(2)
//                        .status(WorkStatus.DONE)
//                        .sessions(Set.of(
//                                Session.builder()
//                                        .status(WorkStatus.DONE)
//                                        .profile(profiles.get(0))
//                                        .start(new Date())
//                                        .end(new Date())
//                                        .build(),
//                                Session.builder()
//                                        .status(WorkStatus.DONE)
//                                        .profile(profiles.get(1))
//                                        .start(new Date())
//                                        .end(new Date())
//                                        .build(),
//                                Session.builder()
//                                        .profile(profiles.get(2))
//                                        .status(WorkStatus.DONE)
//                                        .start(new Date())
//                                        .end(new Date())
//                                        .build()
//                        ))
//                        .isShared(true)
//                        .start(new Date())
//                        .end(new Date())
//                        .build();
//        var saved = groupRepos.save(group);

        assertDoesNotThrow(() -> statisticService.takeStatistic());
    }
}