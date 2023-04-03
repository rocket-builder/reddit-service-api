package com.anthill.ofhelperredditmvc.services.rest;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.repos.ProxyRepos;
import com.anthill.ofhelperredditmvc.repos.RedditAccountProfileRepos;
import com.anthill.ofhelperredditmvc.services.RedisService;
import com.anthill.ofhelperredditmvc.services.parsers.ProxyParser;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProxyService extends AbstractRedisRestService<Proxy, ProxyRepos> {

    private final EntityManager entityManager;
    private final ProxyParser proxyParser;
    private final RedditAccountProfileRepos profileRepos;
    private final UserService userService;

    protected ProxyService(RedisService redis,
                           ProxyRepos repos,
                           EntityManager entityManager,
                           ProxyParser proxyParser,
                           RedditAccountProfileRepos profileRepos,
                           UserService userService) {
        super(redis, repos);
        this.entityManager = entityManager;
        this.proxyParser = proxyParser;
        this.profileRepos = profileRepos;
        this.userService = userService;
    }

    @Override
    public Proxy save(Proxy entity) throws ResourceAlreadyExists, IncorrectInputDataException {
        if(findByFormattedValue(entity.getFormattedValue()).isPresent()){
            throw new ResourceAlreadyExists();
        }

        if(!proxyParser.isCorrect(entity.getFormattedValue())) {
            throw new IncorrectInputDataException("Incorrect proxy passed");
        }

        return super.save(entity);
    }

    @Override
    public Iterable<Proxy> saveAll(Iterable<Proxy> entities) {
        var proxies = StreamSupport.stream(entities.spliterator(), false)
                .filter(p ->
                        findByFormattedValue(p.getFormattedValue()).isEmpty() &&
                                proxyParser.isCorrect(p.getFormattedValue()))
                .collect(Collectors.toList());

        return super.saveAll(proxies);
    }

    @Override
    public Proxy deleteById(long id) throws ResourceNotFoundedException {
        profileRepos.disconnectProxyByProxyId(id);
        userService.disconnectProxyFromUserById(id);

        var res = repos.findById(id)
                .orElseThrow(ResourceNotFoundedException::new);

        redis.hDel(getEntityClassName(), res.hashCodeString());
        repos.deleteByIdNative(id);

        return res;
    }

    @Override
    public Proxy delete(Proxy entity) throws ResourceNotFoundedException {
        return deleteById(entity.getId());
    }

    public Optional<Proxy> findByFormattedValue(String value) {
        var fromRedis = redis.hGet(Proxy.class.getSimpleName(), String.valueOf(value.hashCode()));

        Optional<Proxy> proxy;
        if(fromRedis != null){
            proxy = Optional.of((Proxy) fromRedis);
        } else {
            proxy = repos.findByFormattedValue(value);
        }

        return proxy;
    }

    public Optional<Proxy> findByFormattedValueFromDb(String value) {

        return repos.findByFormattedValue(value);
    }

    public Proxy findByFormattedValueOrCreate(String value) throws IncorrectInputDataException {
        if(!proxyParser.isCorrect(value)) {
            throw new IncorrectInputDataException("Incorrect proxy passed");
        }

        return findByFormattedValueFromDb(value)
                .orElseGet(() -> repos.save(new Proxy(value)));
    }

    @Transactional
    public void deleteAllByUser(User user) {
        user.getProfiles().stream()
                .filter(p -> p.getProxy() != null)
                .peek(p -> p.setProxy(null))
                .forEach(entityManager::persist);

        user.getProxies().forEach(proxy -> {
            redis.hDel(getEntityClassName(), proxy.hashCodeString());
        });

        var userProxies = List.copyOf(user.getProxies());

        user.setProxies(new ArrayList<>());
        entityManager.persist(user);

        userProxies.forEach(entityManager::remove);
    }
}
