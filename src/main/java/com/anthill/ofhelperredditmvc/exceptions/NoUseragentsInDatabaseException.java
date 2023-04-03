package com.anthill.ofhelperredditmvc.exceptions;

public class NoUseragentsInDatabaseException extends RuntimeException {

    public NoUseragentsInDatabaseException(){
        super("No useragents in our database, contact tech support :(");
    }
}
