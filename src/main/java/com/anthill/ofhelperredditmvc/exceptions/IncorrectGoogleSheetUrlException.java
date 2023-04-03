package com.anthill.ofhelperredditmvc.exceptions;

public class IncorrectGoogleSheetUrlException extends RuntimeException {

    public IncorrectGoogleSheetUrlException(){
        super("Incorrect Google Sheet Url passed!");
    }
}
