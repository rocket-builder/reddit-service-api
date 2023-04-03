package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.RedditAccount;
import com.anthill.ofhelperredditmvc.domain.dto.bot.RedditAccountTokenDto;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.RedditAccountRepos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedditAccountService extends AbstractRestService<RedditAccount, RedditAccountRepos> {

    @Value("${reddit.account.service.disreputation_points_limit}")
    private int disreputationPointsLimit;

    protected RedditAccountService(RedditAccountRepos repos) {
        super(repos);
    }

    public RedditAccount updateToken(RedditAccount account, RedditAccountTokenDto tokenDto){
        account.setAccessToken(tokenDto.getAccessToken());
        account.setRefreshToken(tokenDto.getRefreshToken());

        return repos.save(account);
    }

    public RedditAccount addDisreputationPointById(long redditAccountId) throws ResourceNotFoundedException {
        var account = findById(redditAccountId);

        account.setDisreputationPoints(account.getDisreputationPoints() + 1);
        if(account.getDisreputationPoints() >= disreputationPointsLimit){
            account.setReputationBan(true);
        }

        return repos.save(account);
    }

    public RedditAccount addPassedUpVoteById(long redditAccountId) throws ResourceNotFoundedException {
        var account = findById(redditAccountId);

        account.setPassedUpVotes(account.getPassedUpVotes() + 1);

        return repos.save(account);
    }


    public RedditAccount resetReputationById(long redditAccountId) throws ResourceNotFoundedException {
        var account = findById(redditAccountId);

        if(account.getDisreputationPoints() >= disreputationPointsLimit){
            throw new IncorrectInputDataException("Disreputation points limit exceed");
        }

        account.setDisreputationPoints(0);
        account.setReputationBan(false);

        return repos.save(account);
    }
}
