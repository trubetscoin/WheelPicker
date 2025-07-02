package com.wheelpicker.config;

import com.wheelpicker.security.filter.ExceptionHandlerFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class CommonSecurityConfig {

    private final ExceptionHandlerFilter exceptionHandlerFilter;

    public CommonSecurityConfig(ExceptionHandlerFilter exceptionHandlerFilter) {
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    public void applyCommonFilters (HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // JWT acts as csrf in combination with OriginCheckFilter
                .cors(cors -> cors.disable()) // OriginCheckFilter used instead. Same logic
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT session used
                .exceptionHandling(handler -> handler.disable()) // Instead ExceptionHandlerFilter does its job
                .requestCache(cache -> cache.disable()) // React will perform redirection instead of backend
                .anonymous(anon -> anon.disable()) // User either has an Authentication or null
                .logout(logout -> logout.disable()) // Custom logout logic
                .httpBasic(basic -> basic.disable()) // JWT used in app
                .formLogin(form -> form.disable()) // JWT used in app (Disables the default UsernamePasswordAuthenticationFilter from running)
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
