package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface ProxyRepos extends IRepository<Proxy> {

    Optional<Proxy> findByFormattedValue(String value);

    @Modifying
    @Transactional
    @Query("delete from Proxy p where p.id = :id")
    void deleteByIdNative(@Param("id") long id);
}
