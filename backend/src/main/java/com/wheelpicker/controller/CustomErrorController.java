package com.wheelpicker.controller;

import com.wheelpicker.exceptionHandling.ProblemDetailFactory;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ProblemDetail> handleError(HttpServletRequest request) {
        Throwable error = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String message = error != null ? error.getMessage() : "Unknown Internal Error";

        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = statusCode != null ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;

        String errorCode = null;

        if (httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            message = "Page not found";
            errorCode = "PAGE_NOT_FOUND";
        }

        ProblemDetail problem = ProblemDetailFactory.create(
                httpStatus,
                httpStatus.getReasonPhrase(),
                message,
                errorCode != null ? errorCode : "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity.status(httpStatus).body(problem);
    }
}
