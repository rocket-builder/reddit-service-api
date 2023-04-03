package com.anthill.ofhelperredditmvc.exceptions;

public class GroupAlreadyStartedException extends Exception {

    public GroupAlreadyStartedException() {
        super("This group already started!");
    }
}
