package com.wheelpicker.exceptionHandling.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User " + message + " could not be found");
    }
}
