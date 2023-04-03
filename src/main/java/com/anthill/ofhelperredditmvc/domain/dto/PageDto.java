package com.anthill.ofhelperredditmvc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageDto<E> {

    private List<E> data;
    private int totalCount;
}
