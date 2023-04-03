package com.anthill.ofhelperredditmvc.services.group.actions;

import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.bot.AbstractBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.AbstractBotGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.UpVoteBotGroup;
import com.anthill.ofhelperredditmvc.exceptions.*;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRepository;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.repos.SessionRepos;
import com.anthill.ofhelperredditmvc.repos.UpVoteGroupRepos;
import com.anthill.ofhelperredditmvc.services.RedditBotService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class UpVoteGroupActionsService extends AbstractGroupActionsService<UpVoteGroup> {

    private final UserService userService;
    private final RedditAccountProfileRepos profileRepos;
    private final SessionRepos sessionRepos;

    public UpVoteGroupActionsService(UpVoteGroupRepos repos,
                                     RedditBotService botService,
                                     UserService userService,
                                     RedditAccountProfileRepos profileRepos,
                                     SessionRepos sessionRepos,
                                     ModelMapper mapper) {
        super(botService, repos, mapper);
        this.userService = userService;
        this.profileRepos = profileRepos;
        this.sessionRepos = sessionRepos;
    }

    @Override
    public UpVoteGroup start(UpVoteGroup group) throws GroupAlreadyStartedException {
        if(group.getStatus().equals(WorkStatus.WORK)){
            throw new GroupAlreadyStartedException();
        }
        sessionRepos.deleteAllByGroup_Id(group.getId());

        var profiles = group.isShared()?
                profileRepos.findRandomSharedProfilesForUpVote(group.getUpVoteCount()) :
                profileRepos.findRandomProfilesForUpVoteByUserId(group.getUser().getId(), group.getUpVoteCount());

        SortedSet<Session> sessions = profiles.stream()
                .map(profile ->
                        Session.builder()
                                .status(WorkStatus.WORK)
                                .start(new Date())
                                .group(group)
                                .profile(profile)
                                .build())
                .collect(Collectors.toCollection(TreeSet::new));

        if(group.isShared()){
            if(sessions.size() < group.getUpVoteCount()){
                throw new NotEnoughRedditAccountsException();
            }
            if(group.getUser().getUpVoteBalance() < group.getUpVoteCount()){
                throw new InsufficientUpVoteBalanceException();
            }
        }

        group.setStart(new Date());
        group.setEnd(null);
        group.setMessage(null);
        group.setStatus(WorkStatus.WORK);
        group.setSessions(sessions);

        var saved = repos.save(group);
        try {
            var botGroup = mapper.map(saved, getTargetBotClass());
            botService.sendGroup(botGroup);

            if(group.isShared()) {
                userService.withdrawUpVotes(group.getUser(), group.getUpVoteCount());
            }
        } catch (RedditBotServiceException | InsufficientUpVoteBalanceException ex){
            UpVoteGroup.setError(saved, ex.getMessage());
            saved = repos.save(saved);
            sessionRepos.deleteAllByGroup_Id(saved.getId());
        }

        return saved;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <GB extends AbstractBotGroup<SB>, SB extends AbstractBotSession> Class<GB> getTargetBotClass() {
        return (Class<GB>) UpVoteBotGroup.class;
    }
}
