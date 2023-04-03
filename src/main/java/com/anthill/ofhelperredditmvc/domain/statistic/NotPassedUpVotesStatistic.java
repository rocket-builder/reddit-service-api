package com.anthill.ofhelperredditmvc.domain.statistic;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import lombok.*;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Builder
@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NotPassedUpVotesStatistic extends AbstractEntity {

    private long orderedUpVotesCount;
    private long upVotesSessionCount;
    private BigDecimal notPassedUpVotesPercent;
}
