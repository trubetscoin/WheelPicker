package com.wheelpicker.exceptionHandling.exception;

import lombok.Getter;

@Getter
public class CredentialsAlreadyExistsException extends RuntimeException {
    private final String credentialType;
    private final String credentialValue;

    public CredentialsAlreadyExistsException(String credentialType, String credentialValue) {
        super(String.format("The %s %s is already in use", credentialType, credentialValue));
        this.credentialType=credentialType;
        this.credentialValue = credentialValue;
    }

}
