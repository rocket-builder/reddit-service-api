package com.anthill.ofhelperredditmvc.services.statistic;

import com.anthill.ofhelperredditmvc.domain.AccountAccess;
import com.anthill.ofhelperredditmvc.domain.dto.statistic.RedditAccountProfileStatisticDto;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class RedditAccountProfileStatisticService {

    private final RedditAccountProfileRepos profileRepos;

    public RedditAccountProfileStatisticService(RedditAccountProfileRepos profileRepos) {
        this.profileRepos = profileRepos;
    }

    public RedditAccountProfileStatisticDto get() {
        var upVoteAccess = EnumSet.of(AccountAccess.ALL, AccountAccess.UPVOTE);

        return RedditAccountProfileStatisticDto.builder()
                .sharedCount(profileRepos.countAllShared(true))

                .bannedCount(profileRepos.countAllSharedWithBanned(true))
                .notBannedCount(profileRepos.countAllSharedWithBanned(false))

                .reputationBanCount(profileRepos.countAllWorkReadyWithReputationBan(true, upVoteAccess))
                .notReputationBanCount(profileRepos.countAllWorkReadyWithReputationBan(false, upVoteAccess))

                .suspendCount(profileRepos.countAllWorkReadyWithSuspend(true, upVoteAccess))
                .notSuspendCount(profileRepos.countAllWorkReadyWithSuspend(false, upVoteAccess))

                .workReadyCount(profileRepos.countAllWorkReadyFor(upVoteAccess))
                .build();
    }
}
