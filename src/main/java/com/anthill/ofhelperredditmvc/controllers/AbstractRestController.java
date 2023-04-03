package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IRestService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRestController<E extends AbstractEntity> {

    protected final IRestService<E> rest;

    protected AbstractRestController(IRestService<E> rest) {
        this.rest = rest;
    }

    @PostMapping
    public ResponseEntity<E> save(@RequestBody E entity) throws ResourceAlreadyExists {
        E res = rest.save(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<Iterable<E>> saveAll(@RequestBody Iterable<E> entities) {
        var res = rest.saveAll(entities);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<E> update(@RequestBody E entity) throws ResourceNotFoundedException {
        E res = rest.update(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> findById(@PathVariable("id") long id) throws ResourceNotFoundedException {
        E res = rest.findById(id);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<E>> findAll() {
        var res = rest.findAll();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageDto<E>> findAllPageable(
            @PageableDefault(page = 0, size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        var res = rest.findAllPageable(pageable);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<E> deleteById(@PathVariable("id") long id) throws ResourceNotFoundedException {
        var res = rest.deleteById(id);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<E> delete(@RequestBody E entity) throws ResourceNotFoundedException {
        var res = rest.delete(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/list")
    public ResponseEntity<List<E>> deleteAll(@RequestBody List<Long> ids) {
        var res = rest.deleteAllByIds(ids);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}

