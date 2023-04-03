package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.dto.AuthenticationDto;
import com.anthill.ofhelperredditmvc.domain.dto.SignUpDto;
import com.anthill.ofhelperredditmvc.domain.dto.TokenDto;
import com.anthill.ofhelperredditmvc.exceptions.AccessDeniedException;
import com.anthill.ofhelperredditmvc.exceptions.LoginAlreadyTakenException;
import com.anthill.ofhelperredditmvc.exceptions.UserBannedException;
import com.anthill.ofhelperredditmvc.security.jwt.JwtTokenProvider;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                    UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody AuthenticationDto auth) throws UsernameNotFoundException, UserBannedException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(auth.getLogin(), auth.getPassword()));

            var user = userService.findByLogin(auth.getLogin());

            if(user.isBanned()){
                throw new UserBannedException();
            }

            var jwtToken = jwtTokenProvider.create(user.getLogin());
            var token = TokenDto.builder()
                    .login(user.getLogin())
                    .token(jwtToken.getToken())
                    .expirationSeconds(jwtToken.getExpirationSeconds())
                    .roles(user.getRoles())
                    .build();
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (AuthenticationException ex){
            throw new BadCredentialsException("Invalid login or password");
        }
    }

    @SecurityRequirements
    @PostMapping("/signUp")
    public ResponseEntity<TokenDto> signUp(@RequestBody SignUpDto signUp) throws LoginAlreadyTakenException {

        var user = userService.signUp(signUp);

        var jwtToken = jwtTokenProvider.create(user.getLogin());
        var token = TokenDto.builder()
                .login(user.getLogin())
                .token(jwtToken.getToken())
                .expirationSeconds(jwtToken.getExpirationSeconds())
                .roles(user.getRoles())
                .build();

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
