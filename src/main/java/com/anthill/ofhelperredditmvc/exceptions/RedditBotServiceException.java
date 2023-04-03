package com.anthill.ofhelperredditmvc.exceptions;

public class RedditBotServiceException extends Exception {

    public RedditBotServiceException(String message) {
        super("Something wend wrong during work with a bot: " + message);
    }
}
