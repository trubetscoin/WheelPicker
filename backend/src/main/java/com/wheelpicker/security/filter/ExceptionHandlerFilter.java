package com.wheelpicker.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Works in a combination with {@link com.wheelpicker.exceptionHandling.GlobalExceptionHandler} to handle exceptions
 */
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver exceptionResolver;

    public ExceptionHandlerFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ModelAndView mw = exceptionResolver.resolveException(request, response, null, e);
            if (mw == null) {
                request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, e.getCause());
                request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute(RequestDispatcher.ERROR_MESSAGE, e.getMessage());
                request.getRequestDispatcher("/error").forward(request, response);
            }
        }
    }
}
