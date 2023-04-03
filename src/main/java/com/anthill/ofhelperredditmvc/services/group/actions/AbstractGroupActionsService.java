package com.anthill.ofhelperredditmvc.services.group.actions;

import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.group.AbstractGroup;
import com.anthill.ofhelperredditmvc.exceptions.GroupAlreadyStartedException;
import com.anthill.ofhelperredditmvc.exceptions.GroupAlreadyStoppedException;
import com.anthill.ofhelperredditmvc.exceptions.RedditBotServiceException;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRepository;
import com.anthill.ofhelperredditmvc.interfaces.IBotGroupMapperTarget;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import com.anthill.ofhelperredditmvc.services.RedditBotService;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class AbstractGroupActionsService<G extends AbstractGroup> implements IBotGroupMapperTarget {

    protected final RedditBotService botService;
    protected final IAuthenticatedRepository<G> repos;
    protected final ModelMapper mapper;

    public AbstractGroupActionsService(RedditBotService botService,
                                       IAuthenticatedRepository<G> repos,
                                       ModelMapper mapper) {
        this.botService = botService;
        this.repos = repos;
        this.mapper = mapper;
    }

    public abstract G start(G group) throws RedditBotServiceException, GroupAlreadyStartedException;

    public G stop(G group) throws RedditBotServiceException, GroupAlreadyStoppedException {

        if(group.getStatus().equals(WorkStatus.WORK)){
            var stoppedSessions = group.getSessions().stream()
                    .peek(session -> {
                        session.setEnd(new Date());
                        session.setStatus(WorkStatus.DONE);
                    })
                    .collect(Collectors.toCollection(TreeSet::new));

            group.setEnd(new Date());
            group.setStatus(WorkStatus.DONE);
            group.setSessions(stoppedSessions);

            var updatedGroup = repos.save(group);

            var botGroup = mapper.map(updatedGroup, getTargetBotClass());
            botService.sendStop(botGroup);

            return updatedGroup;
        } else {
            throw new GroupAlreadyStoppedException();
        }
    }
}
