package com.anthill.ofhelperredditmvc.services.redditprofile;

import com.anthill.ofhelperredditmvc.domain.AccountAccess;
import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.dto.accounts.RedditAccountProfileDto;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.services.rest.RedditAccountProfileService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedditAccountProfileUpdateService {

    private final RedditAccountProfileRepos repos;
    private final RedditAccountProfileService profileService;

    public RedditAccountProfileUpdateService(RedditAccountProfileRepos repos,
                                             RedditAccountProfileService profileService) {
        this.repos = repos;
        this.profileService = profileService;
    }

    public Iterable<RedditAccountProfile> detachProxies(List<RedditAccountProfile> profiles) {
        if (profiles.isEmpty()) {
            return new ArrayList<>();
        }

        var update = profiles.stream()
                .peek(profile -> profile.setProxy(null))
                .collect(Collectors.toList());

        return repos.saveAll(update);
    }

    public List<RedditAccountProfile> updateListBySingle(
            List<RedditAccountProfile> profiles, RedditAccountProfileDto update) {
        try {
            var updated = new ArrayList<RedditAccountProfile>();

            profiles.forEach(profile -> {
                try {
                    if(!update.getLogin().isEmpty())
                        profile.getRedditAccount().setLogin(update.getLogin());

                    if(!update.getPassword().isEmpty())
                        profile.getRedditAccount().setPassword(update.getPassword());

                    if(!update.getProxy().isEmpty()){
                        profile.setProxy(new Proxy(update.getProxy()));
                    }
                    if(!update.getAccess().isEmpty()){
                        profile.setAccess(AccountAccess.valueOf(update.getAccess()));
                    }
                    if(update.isShared() != profile.isShared()){
                        profile.setShared(update.isShared());
                    }

                    var saved = profileService.update(profile, profile.getUser());
                    updated.add(saved);
                } catch (ResourceNotFoundedException ignored){}
            });

            return updated;
        } catch (NumberFormatException ex){
            throw new IncorrectInputDataException("Incorrect rotate interval");
        }
        catch (IllegalArgumentException ex){
            throw new IncorrectInputDataException("Incorrect access passed");
        }
        catch (NullPointerException ex){
            throw new IncorrectInputDataException("Not enough fields passed");
        }
    }
}
