package com.anthill.ofhelperredditmvc.domain.dto;

import com.anthill.ofhelperredditmvc.domain.RedditAccountFilter;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfileSearchFields;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedditAccountProfileFilterDto {

    private RedditAccountProfileSearchFields by;
    private String value;

    private RedditAccountFilter[] filters;
}
