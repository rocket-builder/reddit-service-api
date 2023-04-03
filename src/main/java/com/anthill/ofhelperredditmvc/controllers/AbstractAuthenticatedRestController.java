package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.AbstractAuthenticatedEntity;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRestService;
import com.anthill.ofhelperredditmvc.services.security.JwtUserRetriever;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class AbstractAuthenticatedRestController<E extends AbstractAuthenticatedEntity> {

    protected final JwtUserRetriever jwt;
    protected final IAuthenticatedRestService<E> rest;

    protected AbstractAuthenticatedRestController(IAuthenticatedRestService<E> rest,
                                                  JwtUserRetriever jwt) {
        this.jwt = jwt;
        this.rest = rest;
    }

    @GetMapping
    public ResponseEntity<List<E>> findAll(HttpServletRequest request){
        var user = jwt.getUserFromRequest(request);

        var entities = rest.findAll(user);

        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageDto<E>> findAllPageable(
            @PageableDefault(page = 0, size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        var user = jwt.getUserFromRequest(request);

        var page = rest.findAllPageable(user, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> findById(@PathVariable("id") long id, HttpServletRequest request)
            throws ResourceNotFoundedException {
        var user = jwt.getUserFromRequest(request);
        var entity = rest.findById(id, user);

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<E> save(@RequestBody E entity, HttpServletRequest request)
            throws ResourceAlreadyExists {
        var user = jwt.getUserFromRequest(request);

        var saved = rest.save(entity, user);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<Iterable<E>> saveAll(@RequestBody Iterable<E> entities, HttpServletRequest request) {
        var user = jwt.getUserFromRequest(request);

        var saved = rest.saveAll(entities, user);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<E> update(@RequestBody E entity, HttpServletRequest request)
            throws ResourceNotFoundedException {
        var user = jwt.getUserFromRequest(request);

        var saved = rest.update(entity, user);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<E> deleteById(@PathVariable("id") long id, HttpServletRequest request)
            throws ResourceNotFoundedException {
        var user = jwt.getUserFromRequest(request);
        var deleted = rest.deleteById(id, user);

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/list")
    public ResponseEntity<List<E>> deleteAllByIds(@RequestBody List<Long> ids, HttpServletRequest request) {
        var user = jwt.getUserFromRequest(request);

        var deleted = rest.deleteAllByIds(ids, user);

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllByUser(HttpServletRequest request){
        var user = jwt.getUserFromRequest(request);

        rest.deleteAllByUser(user);

        return new ResponseEntity<>("Successfully deleted!", HttpStatus.OK);
    }
}
