package com.anthill.ofhelperredditmvc.domain.session.bot;

import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class AbstractBotSession {
    protected long id;

    protected RedditAccountProfile profile;

    protected WorkStatus status;
    protected String message;
}
