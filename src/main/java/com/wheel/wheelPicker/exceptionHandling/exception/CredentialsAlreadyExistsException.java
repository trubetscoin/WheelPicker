package com.wheel.wheelPicker.exceptionHandling.exception;

public class CredentialsAlreadyExistsException extends RuntimeException {
    public CredentialsAlreadyExistsException(String credentialType, String credentialValue) {
        super(String.format("The %s %s is already in use", credentialType, credentialValue));
    }
}
