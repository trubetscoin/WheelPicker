package com.wheel.wheelPicker.exceptionHandling.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username) {
        super("The username '" + username + "' is already in use");
    }
}
