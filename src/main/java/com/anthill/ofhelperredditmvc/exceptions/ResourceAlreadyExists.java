package com.anthill.ofhelperredditmvc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExists extends Exception {

    public ResourceAlreadyExists(String message){
        super(message);
    }
    public ResourceAlreadyExists(){
        super("Resource already exists!");
    }
}
