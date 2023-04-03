package com.anthill.ofhelperredditmvc.interfaces;

import com.anthill.ofhelperredditmvc.domain.session.bot.AbstractBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.AbstractBotGroup;

public interface IBotGroupMapperTarget {

    <GB extends AbstractBotGroup<SB>, SB extends AbstractBotSession> Class<GB> getTargetBotClass();
}
