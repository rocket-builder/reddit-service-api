package com.anthill.ofhelperredditmvc.domain.session;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.session.group.AbstractGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Table(name = "reddit_session")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"group", "rowNumber", "profile"}, callSuper = false)
@Getter @Setter
public class Session extends AbstractEntity {

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    private AbstractGroup group;

    @Column(name = "row_numb")
    private int rowNumber;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private RedditAccountProfile profile;

    @Enumerated(EnumType.STRING)
    private WorkStatus status;

    private String message;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date start;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date end;

    /**
     * Create work session constructor
     * @param group group attached to this session
     * @param profile profile attached to this session
     * @param rowNumber row number in excel
     */
    public Session(AbstractGroup group, RedditAccountProfile profile, int rowNumber) {
        this.group = group;
        this.rowNumber = rowNumber;
        this.profile = profile;
        this.start = new Date();
        this.status = WorkStatus.WORK;
    }
}
