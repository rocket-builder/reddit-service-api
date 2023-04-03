package com.anthill.ofhelperredditmvc.exceptions;

public class NoRedditAccountsException extends RuntimeException {

    public NoRedditAccountsException(){
        super("No reddit accounts found in your account!");
    }
}
