package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.bot.StatusDto;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectGoogleSheetUrlException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.PostingGroupRepos;
import com.anthill.ofhelperredditmvc.services.parsers.GoogleSheetIdUrlParser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostingGroupService extends AbstractAuthenticatedRestService<PostingGroup, PostingGroupRepos> {

    private final EntityManager entityManager;
    private final UserService userService;
    private final GoogleSheetIdUrlParser urlParser;

    public PostingGroupService(PostingGroupRepos repos,
                               EntityManager entityManager,
                               UserService userService,
                               GoogleSheetIdUrlParser urlParser) {
        super(repos);
        this.entityManager = entityManager;
        this.userService = userService;
        this.urlParser = urlParser;
    }

    @Override
    public PostingGroup findById(long id, User user) throws ResourceNotFoundedException {
        var group = super.findById(id, user);

        return repos.findByIdWithSessions(group.getId())
                .orElseThrow(ResourceNotFoundedException::new);
    }

    public PostingGroup findById(long id) throws ResourceNotFoundedException {
        return repos.findByIdWithSessions(id)
                .orElseThrow(ResourceNotFoundedException::new);
    }

    @Override
    public PostingGroup save(PostingGroup entity, User user) throws ResourceAlreadyExists {
        if(!urlParser.isCorrect(entity.getSheetUrl())){
            throw new IncorrectGoogleSheetUrlException();
        }

        return super.save(entity, user);
    }

    @Modifying
    @Transactional
    @Override
    public PostingGroup deleteById(long id, User user) throws ResourceNotFoundedException {
        var group = findById(id, user);

        group.getSessions().forEach(entityManager::remove);
        entityManager.remove(group);

        return group;
    }

    @Modifying
    @Transactional
    @Override
    public List<PostingGroup> deleteAllByIds(List<Long> ids, User user) {
        var groups = new ArrayList<PostingGroup>();

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
        userService.deleteAllExcelGroupsByUserId(user.getId());
    }

    @Override
    protected List<PostingGroup> getEntities(User user) {
        return userService.findByLoginWithExcelGroups(user.getLogin()).getPostingGroups();
    }

    public PostingGroup updateStatus(PostingGroup group, StatusDto status) {
        group.setStatus(status.getStatus());
        group.setMessage(status.getMessage());

        if(status.getStatus().equals(WorkStatus.DONE) || status.getStatus().equals(WorkStatus.ERROR)){
            group.setEnd(new Date());
        }

        return repos.save(group);
    }
}
