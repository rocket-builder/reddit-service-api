package com.anthill.ofhelperredditmvc.services.statistic;

import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.domain.dto.UpVoteGroupDto;
import com.anthill.ofhelperredditmvc.repos.UpVoteGroupRepos;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UpVoteGroupsStatisticService {

    private final UpVoteGroupRepos groupRepos;

    public UpVoteGroupsStatisticService(UpVoteGroupRepos groupRepos) {
        this.groupRepos = groupRepos;
    }

    public PageDto<UpVoteGroupDto> findAllPageable(Pageable pageable) {
        var count = (int) groupRepos.count();

        var data = groupRepos.findAll(pageable).stream()
                .map(g -> new UpVoteGroupDto(g.getUser().getLogin(), g))
                .collect(Collectors.toList());

        return new PageDto<>(data, count);
    }
}
