package com.anthill.ofhelperredditmvc.security.jwt;

import com.anthill.ofhelperredditmvc.domain.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class JwtUserFactory {

    public static JwtUser create(User user){
        return JwtUser.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .firstName("Service")
                .lastName("User")
                .email("example@gmail.com")
                .authorities(
                        user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.name()))
                                .collect(Collectors.toList())
                )
                .enabled(true)
                .lastPasswordResetDate(user.getUpdated())
                .build();
    }
}
