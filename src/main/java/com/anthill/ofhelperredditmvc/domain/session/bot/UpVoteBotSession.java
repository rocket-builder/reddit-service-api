package com.anthill.ofhelperredditmvc.domain.session.bot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
public class UpVoteBotSession extends AbstractBotSession {
}
