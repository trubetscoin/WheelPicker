package com.wheel.wheelPicker.component;

import jakarta.annotation.PostConstruct;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.Filter;
import org.springframework.stereotype.Component;

// For testing purposes
@Component
public class FilterNamesChain {
    private final FilterChainProxy filterChainProxy;

    public FilterNamesChain(FilterChainProxy filterChainProxy) {
        this.filterChainProxy = filterChainProxy;
    }

    @PostConstruct
    public void printFilters() {
        for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
            System.out.println("Chain for: " + chain);
            for (Filter filter : chain.getFilters()) {
                System.out.println("  " + filter.getClass().getName());
            }
            System.out.println();
        }
    }
}
