package com.anthill.ofhelperredditmvc.exceptions;

public class UnknownSearchFieldException extends RuntimeException {

    public UnknownSearchFieldException(){
        super("Unknown search field passed");
    }
}
