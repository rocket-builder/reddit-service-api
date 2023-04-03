package com.anthill.ofhelperredditmvc.services.security;

import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.security.jwt.JwtTokenProvider;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class JwtUserRetriever {

    private final JwtTokenProvider tokenProvider;
    protected final UserService userService;

    public JwtUserRetriever(JwtTokenProvider tokenProvider,
                            UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    public String getLoginFromRequest(HttpServletRequest request){
        var jwtToken = tokenProvider.resolveToken(request);
        return tokenProvider.getLogin(jwtToken.getToken());
    }

    public User getUserFromRequest(HttpServletRequest request){
        var login = getLoginFromRequest(request);
        return userService.findByLogin(login);
    }
}
