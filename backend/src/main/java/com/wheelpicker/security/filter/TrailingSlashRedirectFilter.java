package com.wheelpicker.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TrailingSlashRedirectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        if (uri.length() > 1 && uri.endsWith("/")) {
            String newUri = uri.substring(0, uri.length() - 1);
            String queryString = request.getQueryString();
            if (queryString != null) newUri += "?" + queryString;
            response.sendRedirect(newUri);
            return;
        }
        chain.doFilter(request, response);
    }
}