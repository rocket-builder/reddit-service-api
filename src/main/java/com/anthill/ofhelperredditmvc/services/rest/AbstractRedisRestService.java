package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import com.anthill.ofhelperredditmvc.interfaces.IRestService;
import com.anthill.ofhelperredditmvc.services.RedisService;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractRedisRestService<E extends AbstractEntity, R extends IRepository<E>>
        implements IRestService<E> {

    protected final RedisService redis;
    protected final R repos;

    protected final Class<E> entityClass;

    protected AbstractRedisRestService(RedisService redis, R repos) {
        this.redis = redis;
        this.repos = repos;

        entityClass = (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public E save(E entity) throws ResourceAlreadyExists {
        var saved = repos.save(entity);
        redis.hSet(entity.getClass().getSimpleName(), entity.hashCodeString(), saved);

        return saved;
    }

    @Override
    public Iterable<E> saveAll(Iterable<E> entities) {
        var saved = repos.saveAll(entities);

        Map<String, Object> map = StreamSupport.stream(saved.spliterator(), false)
                .collect(Collectors.toMap(AbstractEntity::hashCodeString, Function.identity()));

        redis.hSetAll(getEntityClassName(), map);

        return saved;
    }

    @Override
    public E update(E entity) throws ResourceNotFoundedException {
        var optional = repos.findById(entity.getId());
        if(optional.isEmpty()){
            throw new ResourceNotFoundedException();
        }
        redis.hDel(entity.getClass().getSimpleName(), optional.get().hashCodeString());
        redis.hSet(entity.getClass().getSimpleName(), entity.hashCodeString(), entity);

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

        redis.hDel(getEntityClassName(), res.hashCodeString());
        repos.deleteById(id);

        return res;
    }

    @Override
    public E delete(E entity) throws ResourceNotFoundedException {
        return deleteById(entity.getId());
    }

    @Override
    public List<E> deleteAll(List<E> entities) {
        entities.forEach(entity -> {
            try {
                deleteById(entity.getId());
            } catch (ResourceNotFoundedException ignored) {}
        });

        return entities;
    }

    @Override
    public List<E> deleteAllByIds(List<Long> ids) {
        var entities = repos.findAllByIds(ids);

        entities.forEach(e ->
                redis.hDel(getEntityClassName(), e.hashCodeString()));
        repos.deleteAllByIds(ids);

        return entities;
    }

    protected String getEntityClassName(){
        return entityClass.getSimpleName();
    }
}
