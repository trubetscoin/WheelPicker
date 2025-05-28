package com.wheelpicker.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

public class CommonSecurityConfig {

    public static HttpSecurity  disableCommonFilters(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // JWT acts as csrf in combination with OriginCheckFilter
                .cors(cors -> cors.disable()) // OriginCheckFilter used instead. Same logic
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT session used
                .exceptionHandling(handler -> handler.disable()) // Instead ExceptionHandlerFilter does its job
                .requestCache(cache -> cache.disable()) // React will perform redirection instead of backend
                .anonymous(anon -> anon.disable()) // User either has an Authentication or null
                .logout(logout -> logout.disable()) // Custom logout logic
                .httpBasic(basic -> basic.disable()) // JWT used in app
                .formLogin(form -> form.disable()); // JWT used in app (Disables the default UsernamePasswordAuthenticationFilter from running)
    }
}
