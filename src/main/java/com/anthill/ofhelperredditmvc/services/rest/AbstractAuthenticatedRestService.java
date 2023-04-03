package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.AbstractAuthenticatedEntity;
import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRepository;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRestService;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.hibernate.type.SortedMapType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractAuthenticatedRestService
        <E extends AbstractAuthenticatedEntity, R extends IAuthenticatedRepository<E>>
            implements IAuthenticatedRestService<E> {

    protected final R repos;

    public AbstractAuthenticatedRestService(R repos) {
        this.repos = repos;
    }

    @Override
    public E save(E entity, User user) throws ResourceAlreadyExists {
        entity.setUser(user);

        return repos.save(entity);
    }

    @Override
    public Iterable<E> saveAll(Iterable<E> entities, User user) {
        var authenticated = StreamSupport.stream(entities.spliterator(), false)
                .peek(e -> e.setUser(user))
                .collect(Collectors.toList());

        return repos.saveAll(authenticated);
    }

    @Override
    public E update(E entity, User user) throws ResourceNotFoundedException {
        var exists = getEntities(user).stream()
                .anyMatch(e -> e.getId() == entity.getId());

        if(!exists){
            throw new ResourceNotFoundedException();
        }

        entity.setUser(user);
        return repos.save(entity);
    }

    @Override
    public E findById(long id, User user) throws ResourceNotFoundedException {

        return getEntities(user).stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(ResourceNotFoundedException::new);
    }

    @Override
    public List<E> findAll(User user) {
        var entities = getEntities(user);
        Collections.reverse(entities);

        return entities;
    }

    @Override
    public PageDto<E> findAllPageable(User user, Pageable pageable) {
        var entities = getEntities(user);

        var data = repos.findAllByUserPageable(user, pageable);

        return new PageDto<>(data, entities.size());
    }

    @Override
    public E deleteById(long id, User user) throws ResourceNotFoundedException {
        var entity = findById(id, user);

        repos.delete(entity);

        return entity;
    }

    @Override
    public E delete(E entity, User user) throws ResourceNotFoundedException {
        return deleteById(entity.getId(), user);
    }

    @Override
    public List<E> deleteAll(List<E> entities, User user) {
        var ids = entities.stream()
                .map(AbstractEntity::getId)
                .collect(Collectors.toList());

        return deleteAllByIds(ids, user);
    }

    @Override
    public List<E> deleteAllByIds(List<Long> ids, User user) {
        var entities = getEntities(user);

        var delete = entities.stream()
                .filter(e -> ids.contains(e.getId()))
                .collect(Collectors.toList());

        var deleteIds = delete.stream()
                .map(AbstractAuthenticatedEntity::getId)
                .collect(Collectors.toList());

        repos.deleteAllByIds(deleteIds);

        return delete;
    }

    protected abstract List<E> getEntities(User user);
}
