package com.anthill.ofhelperredditmvc.exceptions;

public class NoCompatibleRedditAccountsException extends RuntimeException {

    public NoCompatibleRedditAccountsException(){
        super("No compatible reddit accounts found :(");
    }
}
