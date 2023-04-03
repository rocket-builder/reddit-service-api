package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.domain.dto.UpVoteGroupDto;
import com.anthill.ofhelperredditmvc.domain.dto.statistic.RedditAccountProfileStatisticDto;
import com.anthill.ofhelperredditmvc.domain.statistic.NotPassedUpVotesStatistic;
import com.anthill.ofhelperredditmvc.services.statistic.NotPassedUpVotesStatisticService;
import com.anthill.ofhelperredditmvc.services.statistic.RedditAccountProfileStatisticService;
import com.anthill.ofhelperredditmvc.services.statistic.UpVoteGroupsStatisticService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Tag(name = "Statistic")
@RequestMapping("/statistic")
@RestController
public class StatisticController {

    private final RedditAccountProfileStatisticService profileStatisticService;
    private final NotPassedUpVotesStatisticService upVotesStatisticService;
    private final UpVoteGroupsStatisticService upVoteGroupsStatisticService;

    public StatisticController(RedditAccountProfileStatisticService profileStatisticService,
                               NotPassedUpVotesStatisticService upVotesStatisticService,
                               UpVoteGroupsStatisticService upVoteGroupsStatisticService) {
        this.profileStatisticService = profileStatisticService;
        this.upVotesStatisticService = upVotesStatisticService;
        this.upVoteGroupsStatisticService = upVoteGroupsStatisticService;
    }

    @GetMapping("/redditAccountProfile")
    public ResponseEntity<RedditAccountProfileStatisticDto> getRedditAccountProfileStatistic() {
        var statistic = profileStatisticService.get();

        return new ResponseEntity<>(statistic, HttpStatus.OK);
    }

    @GetMapping("/notPassedUpVotes")
    public ResponseEntity<List<NotPassedUpVotesStatistic>> getNotPassedUpVotesStatistics(
            @RequestParam(defaultValue = "1970/01/01") Date start, @RequestParam Date end) {

        var statistic = upVotesStatisticService.getRange(start, end);

        return new ResponseEntity<>(statistic, HttpStatus.OK);
    }

    @GetMapping("/users/upVoteGroups")
    public ResponseEntity<PageDto<UpVoteGroupDto>> findAllPageable(
            @PageableDefault(page = 0, size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        var page = upVoteGroupsStatisticService.findAllPageable(pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
