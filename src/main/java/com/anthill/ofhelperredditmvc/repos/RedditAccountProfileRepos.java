package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.AccountAccess;
import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRepository;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface RedditAccountProfileRepos extends IAuthenticatedRepository<RedditAccountProfile> {

    Optional<RedditAccountProfile> findById(long id);

    List<RedditAccountProfile> findAll();

    List<RedditAccountProfile> findAllByProxy_Id(long proxyId);

    Optional<RedditAccountProfile> findByUserIdAndRedditAccountLoginAndRedditAccountPassword(long userId, String login, String password);

    Optional<RedditAccountProfile> findByUserIdAndRedditAccountLogin(long userId, String login);

    @Query(value = "select * from reddit_account_profile p " +
            "join reddit_account a on p.reddit_account_id = a.id " +
            "where p.user_id = ? and " +
            "a.is_banned = false and " +
            "a.is_suspend = false and " +
            "a.reputation_ban = false and " +
            "(p.access = \"ALL\" or p.access = \"UPVOTE\") " +
            "and p.proxy_id is not null " +
            "order by rand() " +
            "limit ?", nativeQuery = true)
    List<RedditAccountProfile> findRandomProfilesForUpVoteByUserId(long userId, int limit);

    @Query(value = "select * from reddit_account_profile p " +
            "join reddit_account a on p.reddit_account_id = a.id " +
            "where p.is_shared = true " +
            "and a.is_banned = false " +
            "and a.is_suspend = false " +
            "and a.reputation_ban = false " +
            "and (p.access = \"ALL\" or p.access = \"UPVOTE\") " +
            "and p.proxy_id is not null " +
            "order by rand() " +
            "limit ?", nativeQuery = true)
    List<RedditAccountProfile> findRandomSharedProfilesForUpVote(int limit);

    @Query(value = "select p from RedditAccountProfile p where p.isShared = true and " +
                    "p.redditAccount.isBanned = false and " +
                    "p.redditAccount.isSuspend = false and " +
                    "p.redditAccount.reputationBan = false and " +
                    "p.proxy is not null and " +
                    "(p.access = 'ALL' or p.access = 'UPVOTE') and " +
                    "p.isShared = :shared and " +
                    "p.id not in (:ids)")
    List<RedditAccountProfile> findExtraProfilesPageableWithShared(List<Long> ids, boolean shared, Pageable page);

    List<RedditAccountProfile> findAllByProxyIsNullAndUser(User user);

    @Query("select count(p) from RedditAccountProfile p where p.isShared = :shared")
    int countAllShared(boolean shared);

    @Query("select count(p) from RedditAccountProfile p where p.isShared = true and p.redditAccount.isBanned = :banned")
    int countAllSharedWithBanned(boolean banned);

    @Query("select count(p) from RedditAccountProfile p where p.isShared = true " +
            "and p.redditAccount.reputationBan = false and " +
            "p.redditAccount.isSuspend = :suspend and " +
            "p.redditAccount.isBanned = false and " +
            "p.proxy is not null and " +
            "p.access in (:access)")
    int countAllWorkReadyWithSuspend(boolean suspend, EnumSet<AccountAccess> access);

    @Query("select count(p) from RedditAccountProfile p where p.isShared = true " +
            "and p.redditAccount.reputationBan = :reputationBan and " +
            "p.redditAccount.isSuspend = false and " +
            "p.redditAccount.isBanned = false and " +
            "p.proxy is not null and " +
            "p.access in (:access)")
    int countAllWorkReadyWithReputationBan(boolean reputationBan, EnumSet<AccountAccess> access);

    @Query("select count(p) from RedditAccountProfile p where p.isShared = true " +
            "and p.redditAccount.reputationBan = false and " +
            "p.redditAccount.isSuspend = false and " +
            "p.redditAccount.isBanned = false and " +
            "p.proxy is not null and " +
            "p.access in (:access)")
    int countAllWorkReadyFor(EnumSet<AccountAccess> access);

    @Modifying
    @Transactional
    @Query(value = "update RedditAccountProfile p set p.proxy = null where p.proxy.id=:id")
    void disconnectProxyByProxyId(@Param("id") long proxyId);

    int countAllByProxy(Proxy proxy);

    int countAllByUser(User user);

    @Modifying
    @Transactional
    void deleteAllByUser_Id(long id);
}
