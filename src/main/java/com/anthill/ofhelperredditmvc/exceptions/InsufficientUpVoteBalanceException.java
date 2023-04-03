package com.anthill.ofhelperredditmvc.exceptions;

public class InsufficientUpVoteBalanceException extends RuntimeException {

    public InsufficientUpVoteBalanceException(){
        super("Not enough up votes at your balance!");
    }
}
