package com.anthill.ofhelperredditmvc.domain;

import lombok.*;

import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"value"}, callSuper = false)
@Entity
public class Useragent extends AbstractEntity {

    private String value;
}
