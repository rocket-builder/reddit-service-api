package com.anthill.ofhelperredditmvc.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtToken {

    private String token;
    private int expirationSeconds;
}
