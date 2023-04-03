package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import com.anthill.ofhelperredditmvc.interfaces.IRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.util.List;

public class AbstractRestService<E extends AbstractEntity, R extends IRepository<E>>
        implements IRestService<E> {

    protected final R repos;

    public AbstractRestService(R repos) {
        this.repos = repos;
    }

    @Override
    public E save(E entity) throws ResourceAlreadyExists {
        return repos.save(entity);
    }

    @Override
    public Iterable<E> saveAll(Iterable<E> entities) {

        return repos.saveAll(entities);
    }

    @Override
    public E update(E entity) throws ResourceNotFoundedException {
        if(repos.findById(entity.getId()).isEmpty()){
            throw new ResourceNotFoundedException();
        }

        return repos.save(entity);
    }

    @Override
    public E findById(long id) throws ResourceNotFoundedException {

        return repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);
    }

    @Override
    public List<E> findAll() {

        return repos.findAll();
    }

    @Override
    public PageDto<E> findAllPageable(Pageable pageable) {
        var count = (int) repos.count();
        var data = repos.findAll(pageable).toList();

        return new PageDto<>(data, count);
    }

    @Override
    public E deleteById(long id) throws ResourceNotFoundedException {
        var res = repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);

        repos.deleteById(id);

        return res;
    }

    @Override
    public E delete(E entity) throws ResourceNotFoundedException {
        return deleteById(entity.getId());
    }

    @Override
    public List<E> deleteAll(List<E> entities) {
        repos.deleteAll(entities);

        return entities;
    }

    @Override
    public List<E> deleteAllByIds(List<Long> ids) {
        var entities = repos.findAllByIds(ids);

        repos.deleteAllByIds(ids);

        return entities;
    }
}
