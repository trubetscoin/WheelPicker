package com.wheelpicker.exceptionHandling;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;
import java.util.Map;

public class ProblemDetailFactory {

    public static ProblemDetail create(HttpStatus status, String title, String detail, String errorCode) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        problem.setProperty("errorCode", errorCode);
        return problem;
    }

    public static ProblemDetail create(HttpStatus status, String title, String detail, String errorCode, List<Map<String, String>> fieldErrors) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("errors", fieldErrors);
        return problem;
    }
}
