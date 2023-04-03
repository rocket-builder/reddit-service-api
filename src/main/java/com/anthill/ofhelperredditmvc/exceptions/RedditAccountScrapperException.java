package com.anthill.ofhelperredditmvc.exceptions;

public class RedditAccountScrapperException extends RuntimeException {

    public RedditAccountScrapperException(String username, String reason){
        super("Cannot scrap reddit account: " + username + ", because " + reason);
    }
}
