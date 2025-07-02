package com.wheelpicker.exceptionHandling;

import com.wheelpicker.exceptionHandling.exception.*;
import com.wheelpicker.exceptionHandling.exception.UserBanConflictException.UserBanConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
Required and in combination with {@link com.wheelpicker.security.filter.ExceptionHandlerFilter}
 handles exceptions in overriding spring exceptions manner. As Spring security default
 error translating filter doesn't allow for proper error messages in some cases.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException e) {

        List<Map<String, String>> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", Objects.requireNonNull(fieldError.getDefaultMessage())
                ))
                .toList();

        ProblemDetail problemDetail = ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more fields are invalid",
                "VALIDATION_FAILURE",
                errors
        );

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleInvalidRequest(HttpMessageNotReadableException e) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON or Unreadable Request",
                e.getMessage(),
                "REQUEST_FAILURE"
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ProblemDetailFactory.create(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method not allowed",
                "Method is not allowed for the request resource",
                "ILLEGAL_METHOD"
        );
    }

    @ExceptionHandler(ForbiddenOriginException.class)
    public ProblemDetail handleForbiddenOrigin(ForbiddenOriginException e) {
        return ProblemDetailFactory.create(
                HttpStatus.FORBIDDEN,
                "Forbidden Origin",
                e.getMessage(),
                "FORBIDDEN_ORIGIN"
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException e) {
        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                e.getMessage(),
                "AUTH_FAILURE"
        );
    }

    @ExceptionHandler(CredentialsAlreadyExistsException.class)
    public ProblemDetail handleCredentialsAlreadyExists(CredentialsAlreadyExistsException e) {
        return ProblemDetailFactory.create(
                HttpStatus.CONFLICT,
                "Registration Failed",
                e.getMessage(),
                "REGISTRATION_FAILURE"
        );
    }

    @ExceptionHandler(HeaderException.class)
    public ProblemDetail handleHeaderException(HeaderException e) {
        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED,
                "Invalid Header",
                e.getMessage(),
                "REQUEST_FAILURE"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException e) {
        return ProblemDetailFactory.create(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You do not have permission to access this resource",
                "ACCESS_DENIED"
        );
    }

    @ExceptionHandler(UserBannedException.class)
    public ProblemDetail handleBannedUser(UserBannedException e) {
        return ProblemDetailFactory.create(
                HttpStatus.FORBIDDEN,
                "You are banned",
                "You were banned by the administrator",
                "USER_BANNED"
        );
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ProblemDetail handleMissingCookie(MissingRequestCookieException e) {
        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED,
                "Missing Cookie",
                e.getMessage(),
                "REQUEST_FAILURE"
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException e) {
        return ProblemDetailFactory.create(
                HttpStatus.NOT_FOUND,
                "User could not be found",
                e.getMessage(),
                "RESOURCE_NOT_FOUND"
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingRequestParameter(MissingServletRequestParameterException e) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "Request is missing parameter",
                e.getMessage(),
                "REQUEST_FAILURE"
        );
    }

    @ExceptionHandler(UserBanConflictException.class)
    public ProblemDetail handleUserBanConflict(UserBanConflictException e) {
        return ProblemDetailFactory.create(
                HttpStatus.CONFLICT,
                e.getTitle(),
                e.getMessage(),
                "BAN_CONFLICT"
        );
    }
}