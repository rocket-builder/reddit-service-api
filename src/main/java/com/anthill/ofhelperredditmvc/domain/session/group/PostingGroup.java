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
@DiscriminatorValue("posting")
public class PostingGroup extends AbstractGroup {

    private String sheetUrl;
}
