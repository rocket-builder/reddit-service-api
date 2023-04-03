package com.anthill.ofhelperredditmvc.exceptions;

public class IncorrectPasswordException extends Exception {

    public IncorrectPasswordException(){
        super("Incorrect password!");
    }
}
