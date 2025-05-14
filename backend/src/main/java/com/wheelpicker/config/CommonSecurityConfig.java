package com.wheelpicker.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import static org.springframework.security.config.Customizer.withDefaults;

public class CommonSecurityConfig {

    public static HttpSecurity disableCommonFilters(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // OriginCheckFilter handles csrf attacks
                .cors(withDefaults()) // Look at CorsConfig. Allows React frontend hosted on localhost:3000 to access resources
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT session used
                .exceptionHandling(handler -> handler.disable()) // Instead ExceptionHandlerFilter does its job
                .requestCache(cache -> cache.disable()) // React will perform redirection instead of backend
                .anonymous(anon -> anon.disable()) // User either has an Authentication or null
                .logout(logout -> logout.disable()) // Custom filter will perform logout
                .httpBasic(basic -> basic.disable()) // JWT used in app
                .formLogin(form -> form.disable()); // JWT used in app (Disables the default UsernamePasswordAuthenticationFilter from running)
    }
}
