package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRestService<E> {

    E save(E entity) throws ResourceAlreadyExists;
    Iterable<E> saveAll(Iterable<E> entities);

    E update(E entity) throws ResourceNotFoundedException;

    E findById(long id) throws ResourceNotFoundedException;

    List<E> findAll();

    PageDto<E> findAllPageable(Pageable pageable);

    E deleteById(long id) throws ResourceNotFoundedException;

    E delete(E entity) throws ResourceNotFoundedException;

    List<E> deleteAllByIds(List<Long> ids);

    List<E> deleteAll(List<E> entities);
}
