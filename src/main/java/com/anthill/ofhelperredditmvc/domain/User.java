package com.anthill.ofhelperredditmvc.domain;

import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"login", "password"}, callSuper = false)
@Entity
public class User extends AbstractEntity {

    private String login;
    private String password;

    private int upVoteBalance;
    private String telegramTag;
    private long telegramId;
    private boolean banned;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Proxy> proxies;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RedditAccountProfile> profiles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PostingGroup> postingGroups;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UpVoteGroup> upVoteGroups;
}
