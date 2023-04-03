package com.anthill.ofhelperredditmvc.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignUpDto extends AuthenticationDto {

    private String telegramTag;
}
