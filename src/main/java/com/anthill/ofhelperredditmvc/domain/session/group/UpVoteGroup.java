package com.anthill.ofhelperredditmvc.domain.session.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@DiscriminatorValue("upVote")
public class UpVoteGroup extends AbstractGroup {

    private String postUrl;
    private String message;
    private int upVoteCount;
    private boolean isShared;
}
