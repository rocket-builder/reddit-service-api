package com.anthill.ofhelperredditmvc.domain.dto;

import com.anthill.ofhelperredditmvc.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TokenDto {

    private String login, token;
    private List<Role> roles;
    private int expirationSeconds;
}
