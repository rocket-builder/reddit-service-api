package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.*;
import com.anthill.ofhelperredditmvc.security.jwt.JwtTokenProvider;
import com.anthill.ofhelperredditmvc.services.ProxyGoogleSheetsService;
import com.anthill.ofhelperredditmvc.services.rest.ProxyService;
import com.anthill.ofhelperredditmvc.services.rest.RedditAccountProfileService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Proxy")
@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final JwtTokenProvider tokenProvider;
    private final ProxyService rest;
    private final UserService userService;
    private final ProxyGoogleSheetsService proxyGoogleSheetsService;
    private final RedditAccountProfileService profileService;

    protected ProxyController(JwtTokenProvider tokenProvider,
                              ProxyService rest,
                              UserService userService,
                              ProxyGoogleSheetsService proxyGoogleSheetsService,
                              RedditAccountProfileService profileService) {
        this.tokenProvider = tokenProvider;
        this.rest = rest;
        this.userService = userService;
        this.proxyGoogleSheetsService = proxyGoogleSheetsService;
        this.profileService = profileService;
    }

    private User getUserFromRequest(HttpServletRequest request){
        var jwtToken = tokenProvider.resolveToken(request);
        var login = tokenProvider.getLogin(jwtToken.getToken());
        return userService.findByLogin(login);
    }

    @PostMapping
    public ResponseEntity<Proxy> save(@RequestBody Proxy entity, HttpServletRequest request)
            throws ResourceAlreadyExists {
        var user = getUserFromRequest(request);
        Proxy proxy = rest.save(entity);

        userService.connectProxyToUser(user, proxy);

        return new ResponseEntity<>(proxy, HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<Iterable<Proxy>> saveAll(@RequestBody Iterable<Proxy> entities, HttpServletRequest request) {
        var user = getUserFromRequest(request);
        var proxies = rest.saveAll(entities);

        proxies.forEach(proxy -> userService.connectProxyToUser(user, proxy));

        return new ResponseEntity<>(proxies, HttpStatus.OK);
    }

    @PostMapping("/googleSheets")
    public ResponseEntity<Iterable<Proxy>> saveFromGoogleSheets(@RequestParam String sheetsUrl, HttpServletRequest request)
            throws GoogleSheetsAccessException, GoogleSheetsReadException {
        var proxies = proxyGoogleSheetsService.getFromUrl(sheetsUrl);

        return saveAll(proxies, request);
    }

    @PutMapping("/{id}/attachEmptyRedditAccountProfiles")
    public ResponseEntity<Iterable<RedditAccountProfile>> attachEmptyRedditAccountProfiles(
            @PathVariable("id") long proxyId, HttpServletRequest request) throws AccessDeniedException {
        var user = getUserFromRequest(request);

        var proxy = user.getProxies().stream()
                .filter(p -> p.getId() == proxyId)
                .findFirst()
                .orElseThrow(AccessDeniedException::new);

        var profiles = profileService.attachProxyToEmptyProfilesByUser(proxy, user);

        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    @PutMapping("/{id}/migrateAccounts")
    public ResponseEntity<Iterable<RedditAccountProfile>> migrateAccounts(
            @PathVariable("id") long proxyId, @RequestParam String to, HttpServletRequest request)
                throws AccessDeniedException {
        var user = getUserFromRequest(request);

        var fromProxy = user.getProxies().stream()
                .filter(p -> p.getId() == proxyId)
                .findFirst()
                .orElseThrow(AccessDeniedException::new);

        var toProxy = rest.findByFormattedValueOrCreate(to.trim());
        if(!user.getProxies().contains(toProxy)){
            userService.connectProxyToUser(user, toProxy);
        }

        var migrated = profileService.migrateAccountsToProxy(fromProxy, toProxy);

        return new ResponseEntity<>(migrated, HttpStatus.OK);
    }

    @PutMapping("/{id}/detachAllAccounts")
    public ResponseEntity<String> detachAllAccounts(
            @PathVariable("id") long id, HttpServletRequest request) throws AccessDeniedException {
        var user = getUserFromRequest(request);

        var proxy = user.getProxies().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(AccessDeniedException::new);

        profileService.detachAllAccountsFromProxy(proxy);

        return new ResponseEntity<>("Successfully detached!", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Proxy> update(@RequestBody Proxy entity, HttpServletRequest request)
            throws ResourceNotFoundedException, AccessDeniedException {
        var user = getUserFromRequest(request);

        if(!user.getProxies().contains(entity)){
            throw new AccessDeniedException();
        }

        Proxy proxy = rest.update(entity);

        return new ResponseEntity<>(proxy, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proxy> findById(@PathVariable("id") long id, HttpServletRequest request)
            throws ResourceNotFoundedException {
        var user = getUserFromRequest(request);

        var proxy = user.getProxies().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(ResourceNotFoundedException::new);

        return new ResponseEntity<>(proxy, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Proxy>> findAll(HttpServletRequest request) {
        var user = getUserFromRequest(request);
        var proxies = user.getProxies();

        return new ResponseEntity<>(proxies, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageDto<Proxy>> findAllPageable(
            @PageableDefault(page = 0, size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        var user = getUserFromRequest(request);

        var count = user.getProxies().size();

        var proxies = user.getProxies().stream()
                .sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId()))
                .skip(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        var dto = new PageDto<>(proxies, count);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PageDto<Proxy>> searchByFormattedValue(
            @RequestParam String formattedValue,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            HttpServletRequest request){
        var user = getUserFromRequest(request);

        var founded = user.getProxies().stream()
                .filter(proxy -> proxy.getFormattedValue().contains(formattedValue))
                .collect(Collectors.toList());

        var paged = founded.stream()
                .skip(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        var dto = new PageDto<>(paged, founded.size());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/redditAccount/count")
    public ResponseEntity<Integer> getRedditAccountCount(@PathVariable("id") long id, HttpServletRequest request)
            throws ResourceNotFoundedException {
        var user = getUserFromRequest(request);

        var proxy = user.getProxies().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(ResourceNotFoundedException::new);

        var count = profileService.getAccountCountByProxy(proxy);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Proxy> deleteById(@PathVariable("id") long id, HttpServletRequest request)
            throws ResourceNotFoundedException {
        var proxy = findById(id, request).getBody();
        var deleted = rest.delete(proxy);

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Proxy> delete(@RequestBody Proxy entity, HttpServletRequest request)
            throws ResourceNotFoundedException {
        return deleteById(entity.getId(), request);
    }

    @DeleteMapping("/list")
    public ResponseEntity<List<Proxy>> deleteList(@RequestBody List<Long> ids, HttpServletRequest request) {
        List<Proxy> proxies = new ArrayList<>();

        ids.forEach(id -> {
            try {
                var deleted = deleteById(id, request).getBody();
                proxies.add(deleted);
            } catch (ResourceNotFoundedException ignored) {}
        });

        return new ResponseEntity<>(proxies, HttpStatus.OK);
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAll(HttpServletRequest request) {
        var user = getUserFromRequest(request);

        rest.deleteAllByUser(user);
        return new ResponseEntity<>("Successfully deleted!", HttpStatus.OK);
    }
}
