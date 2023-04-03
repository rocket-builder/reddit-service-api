package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.Useragent;
import com.anthill.ofhelperredditmvc.exceptions.NoUseragentsInDatabaseException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.UseragentRepos;
import com.anthill.ofhelperredditmvc.services.RedisService;
import com.anthill.ofhelperredditmvc.services.rest.AbstractRedisRestService;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

@Service
public class UseragentService extends AbstractRedisRestService<Useragent, UseragentRepos> {
    protected UseragentService(RedisService redis, UseragentRepos repos) {
        super(redis, repos);
    }

    public Useragent findRandom() {
        var fromRedis = redis.sRandMember(Useragent.class.getSimpleName());

        Useragent useragent;
        if(fromRedis != null) {
            useragent = (Useragent) fromRedis;
        } else {
            useragent = repos.findRandom()
                    .orElseThrow(NoUseragentsInDatabaseException::new);
        }

        return useragent;
    }

    @Override
    public Useragent save(Useragent entity) throws ResourceAlreadyExists {
        if(repos.findByValue(entity.getValue()).isPresent()){
            throw new ResourceAlreadyExists();
        }

        var saved = repos.save(entity);
        redis.sAdd(Useragent.class.getSimpleName(), saved);

        return saved;
    }

    @Override
    public Iterable<Useragent> saveAll(Iterable<Useragent> entities) {
        var saved = repos.saveAll(entities);

        StreamSupport.stream(saved.spliterator(), false)
                .forEach(s -> redis.sAdd(Useragent.class.getSimpleName(), s));

        return saved;
    }

    @Override
    public Useragent update(Useragent entity) throws ResourceNotFoundedException {
        var old = repos.findById(entity.getId());
        if(old.isEmpty()){
            throw new ResourceNotFoundedException();
        }

        var saved = repos.save(entity);
        redis.sRemove(Useragent.class.getSimpleName(), old.get());
        redis.sAdd(Useragent.class.getSimpleName(), saved);

        return saved;
    }

    @Override
    public Useragent deleteById(long id) throws ResourceNotFoundedException {
        var entity = repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);

        redis.sRemove(Useragent.class.getSimpleName(), entity);
        repos.delete(entity);

        return entity;
    }
}
