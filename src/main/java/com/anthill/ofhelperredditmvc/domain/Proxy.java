package com.anthill.ofhelperredditmvc.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@SuperBuilder
@NoArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
@Entity
public class Proxy extends AbstractEntity {

    @NonNull
    private String formattedValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proxy proxy = (Proxy) o;
        return formattedValue.equals(proxy.formattedValue);
    }

    @Override
    public int hashCode() {
        return formattedValue.hashCode();
    }
}
