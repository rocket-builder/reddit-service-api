package com.anthill.ofhelperredditmvc.exceptions;

public class GoogleSheetsAccessException extends RuntimeException{

    public GoogleSheetsAccessException(){
        super("Cannot get access rights to action with spreadsheet, please sure that you provide write/read access!");
    }
}
