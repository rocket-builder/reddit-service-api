package com.anthill.ofhelperredditmvc.domain.session.group.bot;

import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.anthill.ofhelperredditmvc.domain.session.bot.AbstractBotSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class AbstractBotGroup<S extends AbstractBotSession> {
    protected long id;

    @Enumerated(EnumType.STRING)
    protected WorkStatus status;

    protected String message;

    protected List<S> sessions;
}
