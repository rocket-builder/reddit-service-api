package com.anthill.ofhelperredditmvc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Comparable<AbstractEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Europe/Minsk")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Europe/Minsk")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updated;

    public String hashCodeString(){
        return String.valueOf(this.hashCode());
    }

    @Override
    public int compareTo(AbstractEntity o) {
        return Long.compare(this.id, o.getId());
    }
}
