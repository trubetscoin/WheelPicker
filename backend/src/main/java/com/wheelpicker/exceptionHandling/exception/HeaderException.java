package com.wheelpicker.exceptionHandling.exception;

public class HeaderException extends RuntimeException {
    public HeaderException(String header) {
        super(header);
    }
}
