package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.Useragent;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UseragentRepos extends IRepository<Useragent> {

    Optional<Useragent> findByValue(String value);

    @Query(value = "select * from useragent order by RAND() limit 1", nativeQuery = true)
    Optional<Useragent> findRandom();
}
