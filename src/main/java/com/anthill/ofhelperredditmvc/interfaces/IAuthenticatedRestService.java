package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.AbstractAuthenticatedEntity;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceAlreadyExists;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAuthenticatedRestService<E extends AbstractAuthenticatedEntity>{

    E save(E entity, User user) throws ResourceAlreadyExists;
    Iterable<E> saveAll(Iterable<E> entities, User user);

    E update(E entity, User user) throws ResourceNotFoundedException;

    E findById(long id, User user) throws ResourceNotFoundedException;

    List<E> findAll(User user);

    PageDto<E> findAllPageable(User user, Pageable pageable);

    E deleteById(long id, User user) throws ResourceNotFoundedException;

    E delete(E entity, User user) throws ResourceNotFoundedException;

    List<E> deleteAll(List<E> entities, User user);

    List<E> deleteAllByIds(List<Long> ids, User user);

    void deleteAllByUser(User user);
}
