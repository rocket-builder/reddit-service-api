package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.RedditAccount;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface RedditAccountRepos extends IRepository<RedditAccount> {

    List<RedditAccount> findAllByLogin(String login);

    @Query(value = "select a from RedditAccount a " +
            "where a.isBanned = false and " +
            "((a.isSuspend = true and a.suspendTime is not null) or a.isSuspend = false)")
    List<RedditAccount> findAllAccountsForSync();
}
