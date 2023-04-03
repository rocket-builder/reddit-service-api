package com.anthill.ofhelperredditmvc.exceptions;

public class NotEnoughRedditAccountsException extends RuntimeException {

    public NotEnoughRedditAccountsException() {
        super("Not enough reddit accounts in our database, please contact tech support :(");
    }
}
