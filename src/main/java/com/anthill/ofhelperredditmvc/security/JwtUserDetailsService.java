package com.anthill.ofhelperredditmvc.security;

import com.anthill.ofhelperredditmvc.security.jwt.JwtUserFactory;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var user = userService.findByLogin(login);

        return JwtUserFactory.create(user);
    }
}
