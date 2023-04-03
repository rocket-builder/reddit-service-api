package com.anthill.ofhelperredditmvc.domain;

import com.anthill.ofhelperredditmvc.domain.session.group.PostingGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.UpVoteGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostingGroup.class, name = "excel"),
        @JsonSubTypes.Type(value = UpVoteGroup.class, name = "upVote"),
        @JsonSubTypes.Type(value = RedditAccountProfile.class, name = "profile"),
})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter @Setter
@MappedSuperclass
public abstract class AbstractAuthenticatedEntity extends AbstractEntity {

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE})
    protected User user;
}
