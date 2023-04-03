package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.*;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.repos.RedditAccountRepos;
import com.anthill.ofhelperredditmvc.repos.SessionRepos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class RedditAccountProfileService
        extends AbstractAuthenticatedRestService<RedditAccountProfile, RedditAccountProfileRepos> {

    private final UserService userService;
    private final ProxyService proxyService;
    private final UseragentService useragentService;
    private final RedditAccountRepos redditAccountRepos;
    private final SessionRepos sessionRepos;

    protected RedditAccountProfileService(
            RedditAccountProfileRepos repos,
            UserService userService,
            ProxyService proxyService,
            UseragentService useragentService,
            RedditAccountRepos redditAccountRepos,
            SessionRepos sessionRepos) {
        super(repos);
        this.userService = userService;
        this.proxyService = proxyService;
        this.useragentService = useragentService;
        this.redditAccountRepos = redditAccountRepos;
        this.sessionRepos = sessionRepos;
    }

    @Override
    public RedditAccountProfile save(RedditAccountProfile profile, User user) throws ResourceAlreadyExists {
        profile.setUser(user);
        var exists = repos
                .findByUserIdAndRedditAccountLoginAndRedditAccountPassword(
                        profile.getUser().getId(),
                        profile.getRedditAccount().getLogin(),
                        profile.getRedditAccount().getPassword());
        if(exists.isPresent()){
            throw new ResourceAlreadyExists();
        }

        return saveOrUpdate(profile,true);
    }

    @Override
    public Iterable<RedditAccountProfile> saveAll(Iterable<RedditAccountProfile> entities, User user) {

        var saved = new ArrayList<RedditAccountProfile>();
        for (var entity : entities){
            try {
                var profile = save(entity, user);
                saved.add(profile);
            } catch (ResourceAlreadyExists ignored){}
        }

        return saved;
    }

    @Override
    public RedditAccountProfile update(RedditAccountProfile profile, User user) throws ResourceNotFoundedException {
        if(repos.findById(profile.getId()).isPresent()) {

            profile.setUser(user);
            return saveOrUpdate(profile,false);
        }

        throw new ResourceNotFoundedException();
    }

    private void createRedditAccount(RedditAccountProfile profile) {
        if(isNull(profile.getRedditAccount().getUseragent()) || profile.getRedditAccount().getUseragent().isBlank()){
            var useragent = useragentService.findRandom();
            profile.getRedditAccount().setUseragent(useragent.getValue());
        } else {
            try {
                var useragent = profile.getRedditAccount().getUseragent();
                useragentService.save(new Useragent(useragent));
            } catch (ResourceAlreadyExists ignored){}
        }

        var saved = redditAccountRepos.save(profile.getRedditAccount());
        profile.setRedditAccount(saved);
    }

    private RedditAccountProfile saveOrUpdate(RedditAccountProfile profile, boolean needSearchProxyInRedis){
        var redditAccount = profile.getRedditAccount();
        var sameRedditAccounts = redditAccountRepos.findAllByLogin(redditAccount.getLogin());

        var equalRedditAccount = sameRedditAccounts.stream()
                .filter(account -> account.getPassword().equals(redditAccount.getPassword()))
                .findFirst();

        equalRedditAccount.ifPresentOrElse(profile::setRedditAccount, () -> {
            sameRedditAccounts.stream().findFirst()
                    .ifPresentOrElse(account -> {
                                redditAccount.setBanned(account.isBanned());
                                redditAccount.setKarma(account.getKarma());
                                createRedditAccount(profile);
                            },
                            () -> createRedditAccount(profile));
        });

        if(profile.getProxy() != null) {
            //todo replace this crutch to normal solution and avoid Multiple representations of the same entity exception
            var proxyOptional = needSearchProxyInRedis?
                    proxyService.findByFormattedValue(profile.getProxy().getFormattedValue()) :
                    proxyService.findByFormattedValueFromDb(profile.getProxy().getFormattedValue());

            proxyOptional.ifPresentOrElse(profile::setProxy, () -> {
                        try {
                            var saved = proxyService.save(profile.getProxy());
                            profile.setProxy(saved);

                            userService.connectProxyToUser(profile.getUser(), profile.getProxy());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            profile.setProxy(null);
                        }
                    });
        }

        return repos.save(profile);
    }

    public Iterable<RedditAccountProfile> attachProxyToEmptyProfilesByUser(Proxy proxy, User user){
        var profiles = repos.findAllByProxyIsNullAndUser(user);

        var attached = profiles.stream()
                .peek(profile -> profile.setProxy(proxy))
                .collect(Collectors.toList());

        return repos.saveAll(attached);
    }

    public Iterable<RedditAccountProfile> migrateAccountsToProxy(Proxy from, Proxy to){
        var profiles = repos.findAllByProxy_Id(from.getId());

        if(profiles.isEmpty()){
            return profiles;
        }
        if(profiles.get(0).getProxy().getId() == to.getId()){
            return new ArrayList<>();
        }

        var migration = profiles.stream()
                .peek(profile -> profile.setProxy(to))
                .collect(Collectors.toList());

        return repos.saveAll(migration);
    }

    public void detachAllAccountsFromProxy(Proxy proxy){
        repos.disconnectProxyByProxyId(proxy.getId());
    }

    public int getAccountCountByProxy(Proxy proxy){
        return repos.countAllByProxy(proxy);
    }

    @Override
    public void deleteAllByUser(User user) {
        repos.deleteAllByUser_Id(user.getId());
    }

    @Override
    public List<RedditAccountProfile> deleteAllByIds(List<Long> ids, User user) {
        var entities = getEntities(user);

        var delete = entities.stream()
                .filter(e -> ids.contains(e.getId()))
                .collect(Collectors.toList());

        var deleteIds = delete.stream()
                .map(AbstractAuthenticatedEntity::getId)
                .collect(Collectors.toList());

        sessionRepos.deleteAllByProfileIds(deleteIds);
        repos.deleteAllByIds(deleteIds);

        return delete;
    }

    @Override
    protected List<RedditAccountProfile> getEntities(User user) {
        return user.getProfiles();
    }
}
