package com.anthill.ofhelperredditmvc.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class NewsletterDto {

    private String message;
    private List<Long> telegramIds;
}
