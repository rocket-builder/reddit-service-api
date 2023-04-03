package com.anthill.ofhelperredditmvc.services.statistic;

import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.anthill.ofhelperredditmvc.domain.statistic.NotPassedUpVotesStatistic;
import com.anthill.ofhelperredditmvc.repos.NotPassedUpVotesPercentStatisticRepos;
import com.anthill.ofhelperredditmvc.repos.UpVoteGroupRepos;
import com.anthill.ofhelperredditmvc.utils.DateWithoutTime;
import com.anthill.ofhelperredditmvc.utils.PercentageCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotPassedUpVotesStatisticService {

    @Value("${statistic.up-votes.round_places}")
    private int roundPlaces;

    private final NotPassedUpVotesPercentStatisticRepos repos;
    private final UpVoteGroupRepos upVoteGroupRepos;
    private final PercentageCalculator percentageCalculator;

    public NotPassedUpVotesStatisticService(NotPassedUpVotesPercentStatisticRepos repos,
                                            UpVoteGroupRepos upVoteGroupRepos,
                                            PercentageCalculator percentageCalculator) {
        this.repos = repos;
        this.upVoteGroupRepos = upVoteGroupRepos;
        this.percentageCalculator = percentageCalculator;
    }

    public List<NotPassedUpVotesStatistic> getRange(Date start, Date end){
        return repos.findAllByDateRange(start, end);
    }

    @Scheduled(cron = "0 0 */6 * * *", zone = "Europe/Minsk")
    public void takeStatistic() {
        var today = DateWithoutTime.getNowDate();

        var doneTodayGroups = upVoteGroupRepos.findDoneTodayGroupsWithSessionsByStatus(today);

        var statistic = NotPassedUpVotesStatistic.builder()
                .notPassedUpVotesPercent(BigDecimal.ZERO)
                .orderedUpVotesCount(0)
                .upVotesSessionCount(0)
                .build();

        if(!doneTodayGroups.isEmpty()){
            var orderedUpVotesCount = doneTodayGroups.stream()
                    .mapToInt(UpVoteGroup::getUpVoteCount)
                    .sum();

            var test = doneTodayGroups.stream()
                    .map(g -> g.getUser().getLogin())
                    .collect(Collectors.joining(","));

            var totalUpVoteSessionCount = doneTodayGroups.stream()
                    .mapToLong(g ->
                            g.getSessions().stream()
                                    .filter(s -> s.getStatus().equals(WorkStatus.DONE))
                                    .count())
                    .sum();

            var notPassedUpVotesPercent = BigDecimal.valueOf(100).subtract(
                    percentageCalculator.calculatePercentage(orderedUpVotesCount, totalUpVoteSessionCount, roundPlaces));

            statistic.setNotPassedUpVotesPercent(notPassedUpVotesPercent);
            statistic.setOrderedUpVotesCount(orderedUpVotesCount);
            statistic.setUpVotesSessionCount(totalUpVoteSessionCount);
        }

        var saved = repos.save(statistic);

        log.info("Take not passed up votes statistic: " + saved);
    }
}
