package com.anthill.ofhelperredditmvc.domain;

import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = {"redditAccount"}, callSuper = true)
@Entity
public class RedditAccountProfile extends AbstractAuthenticatedEntity {

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private RedditAccount redditAccount;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Proxy proxy;

    @JsonIgnore
    @OneToMany(mappedBy = "profile", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Session> sessions;

    @Enumerated(EnumType.STRING)
    private AccountAccess access;

    private boolean isShared;
}
