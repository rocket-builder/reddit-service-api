package com.anthill.ofhelperredditmvc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"login", "password"}, callSuper = false)
@Entity
public class RedditAccount extends AbstractEntity {

    @NonNull
    private String login;

    @NonNull
    private String password;

    private String accessToken, refreshToken;
    private boolean isBanned, isSuspend;

    private int disreputationPoints;
    private boolean reputationBan;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Europe/Minsk")
    @Temporal(TemporalType.TIMESTAMP)
    private Date suspendTime;

    private int karma;
    private int passedUpVotes;

    private String useragent;
}
