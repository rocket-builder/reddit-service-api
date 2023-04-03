package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.bot.StatusDto;
import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.bot.UpVoteBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.repos.UpVoteGroupRepos;
import com.anthill.ofhelperredditmvc.services.parsers.RedditPostIdUrlParser;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpVoteGroupService extends AbstractAuthenticatedRestService<UpVoteGroup, UpVoteGroupRepos> {

    private final SessionService sessionService;
    private final ModelMapper mapper;
    private final EntityManager entityManager;
    private final UserService userService;
    private final RedditPostIdUrlParser urlParser;
    private final RedditAccountProfileRepos profileRepos;

    public UpVoteGroupService(UpVoteGroupRepos repos,
                              SessionService sessionService, ModelMapper mapper,
                              EntityManager entityManager,
                              UserService userService,
                              RedditPostIdUrlParser urlParser,
                              RedditAccountProfileRepos profileRepos) {
        super(repos);
        this.sessionService = sessionService;
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.userService = userService;
        this.urlParser = urlParser;
        this.profileRepos = profileRepos;
    }

    @Override
    public UpVoteGroup findById(long id, User user) throws ResourceNotFoundedException {
        var group = super.findById(id, user);

        return repos.findByIdWithSessions(group.getId())
                .orElseThrow(ResourceNotFoundedException::new);
    }

    public UpVoteGroup findById(long id) throws ResourceNotFoundedException {
        return repos.findByIdWithSessions(id)
                .orElseThrow(ResourceNotFoundedException::new);
    }

    @Modifying
    @Transactional
    @Override
    public UpVoteGroup deleteById(long id, User user) throws ResourceNotFoundedException {
        var group = findById(id, user);

        group.getSessions().forEach(entityManager::remove);
        entityManager.remove(group);

        return group;
    }

    @Modifying
    @Transactional
    @Override
    public List<UpVoteGroup> deleteAllByIds(List<Long> ids, User user) {
        var groups = new ArrayList<UpVoteGroup>();

        ids.forEach(id -> repos.findByIdWithSessions(id).ifPresent(groups::add));

        var sessions = groups.stream()
                .flatMap(group -> group.getSessions().stream())
                .collect(Collectors.toList());

        sessions.forEach(entityManager::remove);
        groups.forEach(entityManager::remove);

        return groups;
    }

    @Override
    public void deleteAllByUser(User user) {
        userService.deleteAllUpVoteGroupsByUserId(user.getId());
    }

    @Override
    public UpVoteGroup save(UpVoteGroup entity, User user) throws ResourceAlreadyExists {
        if(!urlParser.isCorrect(entity.getPostUrl())){
            throw new IncorrectInputDataException("Incorrect reddit url passed!");
        }
        if(entity.getUpVoteCount() <= 0){
            throw new IncorrectInputDataException("Incorrect Up Vote count passed!");
        }

        entity.setName("Order_" + new Date().getTime());

        return super.save(entity, user);
    }

    @Override
    protected List<UpVoteGroup> getEntities(User user) {
        return userService.findByLoginWithUpVoteGroups(user.getLogin()).getUpVoteGroups();
    }

    public UpVoteGroup updateStatus(UpVoteGroup group, StatusDto status) {
        group.setStatus(status.getStatus());
        group.setMessage(status.getMessage());

        if(status.getStatus().equals(WorkStatus.DONE) || status.getStatus().equals(WorkStatus.ERROR)){
            group.setEnd(new Date());
        }

        return repos.save(group);
    }

    public List<RedditAccountProfile> getExtraProfiles(UpVoteGroup group, int count) {

        var ids = group.getSessions().stream()
                .map(s -> s.getProfile().getId())
                .collect(Collectors.toList());

        return profileRepos.findExtraProfilesPageableWithShared(ids, group.isShared(), PageRequest.of(0, count));
    }

    public List<UpVoteBotSession> createExtraSessionsForBot(UpVoteGroup group, int count) {

        List<Session> sessions = getExtraProfiles(group, count).stream()
                .map(profile -> Session.builder()
                        .group(group)
                        .profile(profile)
                        .status(WorkStatus.WORK)
                        .build())
                .collect(Collectors.toList());

        var saved = sessionService.saveAll(sessions);

        return mapper.map(saved, new TypeToken<List<UpVoteBotSession>>() {}.getType());
    }
}
