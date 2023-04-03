package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.AbstractAuthenticatedEntity;
import com.anthill.ofhelperredditmvc.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface IAuthenticatedRepository<E extends AbstractAuthenticatedEntity> extends IRepository<E> {

    @Query("select e from #{#entityName} e where e.user = :user")
    List<E> findAllByUserPageable(User user, Pageable pageable);
}
