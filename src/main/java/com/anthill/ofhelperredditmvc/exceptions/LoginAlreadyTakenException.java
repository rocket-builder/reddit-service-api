package com.anthill.ofhelperredditmvc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class LoginAlreadyTakenException extends ResourceAlreadyExists {

    public LoginAlreadyTakenException(){
        super("Login already taken!");
    }
}
