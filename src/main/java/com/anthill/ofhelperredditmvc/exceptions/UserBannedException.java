package com.anthill.ofhelperredditmvc.exceptions;

public class UserBannedException extends Exception {
    
    public UserBannedException(){
        super("Your account have ban at now, please contact the support!");
    }
}
