package com.anthill.ofhelperredditmvc.exceptions;

public class SessionServiceParseSessionException extends RuntimeException {
    public SessionServiceParseSessionException(String reason){
        super("Parse session error, please sure that you fill session template correctly and check spreadsheet for errors, reason: " + reason) ;
    }
}
