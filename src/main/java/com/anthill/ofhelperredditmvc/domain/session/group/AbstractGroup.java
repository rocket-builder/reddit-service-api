package com.anthill.ofhelperredditmvc.domain.session.group;

import com.anthill.ofhelperredditmvc.domain.AbstractAuthenticatedEntity;
import com.anthill.ofhelperredditmvc.domain.session.Session;
import com.anthill.ofhelperredditmvc.domain.session.WorkStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.*;

@Table(name = "session_group")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="session_type",
        discriminatorType = DiscriminatorType.STRING)
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public abstract class AbstractGroup extends AbstractAuthenticatedEntity {

    protected String name;

    @Enumerated(EnumType.STRING)
    protected WorkStatus status;
    protected String message;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date start;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date end;

    @PrePersist
    public void prePersist(){
        status = WorkStatus.CREATED;
    }

    @OneToMany(mappedBy = "group", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @SortNatural
    protected SortedSet<Session> sessions = new TreeSet<>();

    /**
     * Setter for error in groups
     * @param errorMessage message with error
     */
    public static <G extends AbstractGroup> void setError(G group, String errorMessage) {
        group.setStart(null);
        group.setEnd(null);
        group.setMessage(errorMessage);
        group.setSessions(new TreeSet<>());
        group.setStatus(WorkStatus.ERROR);
    }
}
