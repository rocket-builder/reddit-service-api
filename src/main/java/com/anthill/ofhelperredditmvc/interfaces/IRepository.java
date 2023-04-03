package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

@NoRepositoryBean
public interface IRepository<E extends AbstractEntity> extends PagingAndSortingRepository<E, Long> {

    List<E> findAll();

    @Modifying
    @Transactional
    @Query("delete from #{#entityName} e where e.id in (:ids)")
    void deleteAllByIds(List<Long> ids);

    @Query("select e from #{#entityName} e where e.id in (:ids)")
    List<E> findAllByIds(List<Long> ids);
}
