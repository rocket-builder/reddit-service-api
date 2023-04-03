package com.anthill.ofhelperredditmvc.services;

import com.anthill.ofhelperredditmvc.domain.RedditAccount;
import com.anthill.ofhelperredditmvc.exceptions.RedditAccountScrapperException;
import com.anthill.ofhelperredditmvc.repos.RedditAccountRepos;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.net.ConnectException;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedditAccountSyncService {

    private final EntityManager entityManager;
    private final RedditAccountScrapperService scrapperService;
    private final RedditAccountRepos redditAccountRepos;

    @Value("${reddit.scrapper.retry_count}")
    private int scrapperRetryCount;

    public RedditAccountSyncService(EntityManager entityManager,
                                    RedditAccountScrapperService scrapperService,
                                    RedditAccountRepos redditAccountRepos) {
        this.entityManager = entityManager;
        this.scrapperService = scrapperService;
        this.redditAccountRepos = redditAccountRepos;
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Minsk")
    public void syncAccountsWithReddit() {
        log.info("Start sync accounts with reddit");

        var accounts = redditAccountRepos.findAllAccountsForSync();
        log.info("Loaded " + accounts.size() + " accounts for sync, run check procedure");

        if(!accounts.isEmpty()){
            var updatedAccounts = accounts.parallelStream().map(account -> {
                try {
                    var scrapped = scrapperService.scrapWithRetry(account.getLogin(), scrapperRetryCount);

                    if(account.isBanned() != scrapped.isBanned() ||
                            account.isSuspend() != scrapped.isSuspend() ||
                            account.getKarma() != scrapped.getKarma() ||
                            (account.getSuspendTime() != null && new Date().after(account.getSuspendTime()))
                    ) {
                        account.setBanned(scrapped.isBanned());
                        account.setKarma(scrapped.getKarma());

                        if(scrapped.isSuspend())
                            account.setSuspend(scrapped.isSuspend());

                        if(account.getSuspendTime() != null) {
                            var isSuspendTimeExpired = new Date().after(account.getSuspendTime());

                            if(isSuspendTimeExpired) account.setSuspendTime(null);

                            account.setSuspend(!isSuspendTimeExpired);
                        }

                        return Optional.of(account);
                    } else {
                        return Optional.empty();
                    }
                } catch (RedditAccountScrapperException ex){
                    log.error("Error during scrap: " + ex.getMessage());
                    return Optional.empty();
                }
            })
                    .filter(Optional::isPresent)
                    .map(o -> (RedditAccount) o.get())
                    .collect(Collectors.toList());

            if(!updatedAccounts.isEmpty()){
                log.info("Accounts was checked, start update");

                updatedAccounts.forEach(entityManager::persist);
            } else {
                log.info("No updates for accounts");
            }
        } else {
            log.info("Not found accounts for sync");
        }
    }
}
