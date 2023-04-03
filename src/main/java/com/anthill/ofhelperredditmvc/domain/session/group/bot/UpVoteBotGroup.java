package com.anthill.ofhelperredditmvc.domain.session.group.bot;

import com.anthill.ofhelperredditmvc.domain.session.bot.UpVoteBotSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class UpVoteBotGroup extends AbstractBotGroup<UpVoteBotSession> {

    private String postUrl;
    private int upVoteCount;
}
