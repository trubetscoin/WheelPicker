package com.wheel.wheelPicker.exceptionHandling.exception;

public class ForbiddenOriginException extends RuntimeException {
    public ForbiddenOriginException(String origin) {
        super("The forbidden origin: " + origin);
    }
}
