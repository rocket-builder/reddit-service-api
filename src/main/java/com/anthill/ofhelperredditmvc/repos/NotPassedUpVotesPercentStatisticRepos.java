package com.anthill.ofhelperredditmvc.repos;

import com.anthill.ofhelperredditmvc.domain.statistic.NotPassedUpVotesStatistic;
import com.anthill.ofhelperredditmvc.interfaces.IRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface NotPassedUpVotesPercentStatisticRepos extends IRepository<NotPassedUpVotesStatistic> {

    @Query(value = "select s from NotPassedUpVotesStatistic s where s.created >= :start and s.created <= :end")
    List<NotPassedUpVotesStatistic> findAllByDateRange(Date start, Date end);
}
