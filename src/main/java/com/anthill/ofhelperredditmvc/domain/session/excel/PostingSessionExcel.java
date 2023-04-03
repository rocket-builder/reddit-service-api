package com.anthill.ofhelperredditmvc.domain.session.excel;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class PostingSessionExcel extends AbstractSessionExcel {

    private RedditAccountProfile profile;

    private Proxy proxy;

    private String subReddit, title, imageUrl, comment, flairs;
    private int upVoteCount;
}
