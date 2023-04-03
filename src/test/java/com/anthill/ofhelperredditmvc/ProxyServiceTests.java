package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.services.rest.ProxyService;
import com.anthill.ofhelperredditmvc.services.rest.RedditAccountProfileService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProxyServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private RedditAccountProfileService profileService;

    @Test
    void createProxy_whenAllCorrect_thenCreate() throws ResourceAlreadyExists {
        var proxy = new Proxy("https://143.434.123.1:2000@user:password");

        var saved = proxyService.save(proxy);

        assert saved.getId() > 0;
    }

    @Test
    void findByFormattedValue_whenExists_thenFind(){
        var value = "proxy1";

        var proxy = proxyService.findByFormattedValue(value);

        assert proxy.isPresent();
    }

    @Test
    void findAll(){
        var all = proxyService.findAll();

        assert !all.isEmpty();
    }

    @Test
    void getFromRedisAndDb_whenAllCorrect_thenGet(){
        var fromRedis = proxyService.findByFormattedValue("proxy1");
        var fromDb = proxyService.findByFormattedValueFromDb("proxy1");

        assert fromDb.get().equals(fromRedis.get());
    }

    @Test
    void setProxyFromRedis_whenAllCorrect_thenSet() throws ResourceNotFoundedException {
        var user = userService.findByLogin("user");

        var profile = profileService.findById(111925, user);
        var fromRedis = new Proxy("https://bskyqlkn:rmwoer8r9w3a@104.232.209.2:5960");//proxyService.findByFormattedValue("proxy1").get();

        profile.setProxy(fromRedis);
        var updated = profileService.update(profile, user);

        assert updated.getProxy().equals(fromRedis);
    }

    @Test
    void hashCodeTest(){

        var value = "https://143.434.123.1:2000@user:password";

        var hashCode1 = value.hashCode();
        var hashCode2 = new Proxy(value).hashCode();

        assert hashCode1 == hashCode2;
    }
}
