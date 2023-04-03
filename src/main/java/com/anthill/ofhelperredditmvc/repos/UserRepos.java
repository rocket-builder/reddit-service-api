package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepos extends IRepository<User> {

    Optional<User> findByTelegramTag(String tag);
    Optional<User> findByLogin(String login);

    @Query("select s.group.user from Session s where s.id=:id")
    Optional<User> findUserBySessionId(long id);

    @Transactional
    @Modifying
    @Query(value = "insert into user_proxies values(?, ?)", nativeQuery = true)
    void connectProxyToUser(long userId, long proxyId);

    @Transactional
    @Modifying
    @Query(value = "delete from user_proxies up where up.proxies_id = ?", nativeQuery = true)
    void disconnectProxyFromUser(long proxyId);

    @Transactional
    @Modifying
    @Query(value = "delete from user_proxies up where up.proxies_id in (:ids)", nativeQuery = true)
    void disconnectProxiesFromUser(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "delete from user_roles ur where ur.user_id = ?", nativeQuery = true)
    void deleteRolesByUserId(long userId);

    @Query(value = "select u from User u left join u.profiles where u.login=:login")
    Optional<User> findByLoginWithProfiles(@Param("login") String login);

    @Query("select u from User u left join fetch u.postingGroups g where u.login=:login")
    Optional<User> findByLoginWithExcelGroups(String login);

    @Query("select u from User u left join fetch u.upVoteGroups g where u.login=:login")
    Optional<User> findByLoginWithUpVoteGroups(String login);

    @Query("select u.upVoteBalance from User u where u.telegramId = :telegramId")
    Optional<Integer> findUpVoteBalanceByTelegramId(long telegramId);

    @Query("select u.telegramId from User u where u.telegramId <> 0")
    List<Long> findAllTelegramIdsForNewsletter();
}
