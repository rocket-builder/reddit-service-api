package com.anthill.ofhelperredditmvc.exceptions;

public class GoogleSheetsReadException extends RuntimeException {

    public GoogleSheetsReadException(String reason){
        super("Cannot read spreadsheet because: " + reason);
    }
}
