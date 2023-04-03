package com.anthill.ofhelperredditmvc.domain.dto.statistic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RedditAccountProfileStatisticDto {

    private int sharedCount;

    private int bannedCount, notBannedCount;

    private int suspendCount, notSuspendCount;

    private int reputationBanCount, notReputationBanCount;

    private int workReadyCount;
}
