package com.anthill.ofhelperredditmvc.exceptions;

public class TelegramBotServiceException extends Exception {

    public TelegramBotServiceException(String message){
        super("Telegram service error: " + message);
    }
}
