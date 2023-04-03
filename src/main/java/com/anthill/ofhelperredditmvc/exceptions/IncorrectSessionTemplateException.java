package com.anthill.ofhelperredditmvc.exceptions;

public class IncorrectSessionTemplateException extends RuntimeException {

    public IncorrectSessionTemplateException(){
        super("Incorrect session template!");
    }
}
