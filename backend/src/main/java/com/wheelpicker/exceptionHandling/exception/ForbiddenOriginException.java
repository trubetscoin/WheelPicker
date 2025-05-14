package com.wheelpicker.exceptionHandling.exception;

public class ForbiddenOriginException extends RuntimeException {
    public ForbiddenOriginException(String origin) {
        super("The forbidden origin: " + origin);
    }
}
