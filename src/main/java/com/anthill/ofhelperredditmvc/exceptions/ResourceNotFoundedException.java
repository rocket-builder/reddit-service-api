package com.anthill.ofhelperredditmvc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundedException extends Exception {

    public ResourceNotFoundedException(){
        super("Resource not founded!");
    }
}
