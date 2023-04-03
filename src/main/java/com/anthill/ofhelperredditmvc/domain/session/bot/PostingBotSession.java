package com.anthill.ofhelperredditmvc.domain.session.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostingBotSession extends AbstractBotSession {

    private String subReddit, title, imageUrl, comment, flairs;
    private int upVoteCount;
}
