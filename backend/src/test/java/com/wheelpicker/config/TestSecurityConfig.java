package com.wheelpicker.config;

import com.wheelpicker.security.filter.ExceptionHandlerFilter;
import com.wheelpicker.security.filter.IsUserBannedFilter;
import com.wheelpicker.security.filter.JwtFilter;
import com.wheelpicker.security.filter.OriginCheckFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain exceptionHandlerChain(HttpSecurity http, ExceptionHandlerFilter exceptionHandlerFilter) throws Exception {
        CommonSecurityConfig.disableCommonFilters(http);
        http
                .securityMatcher("/test/exceptionHandler/**")
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class) // Global exception handler
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http, ExceptionHandlerFilter exceptionHandlerFilter, JwtFilter jwtFilter) throws Exception {
        CommonSecurityConfig.disableCommonFilters(http);
        http
                .securityMatcher("/test/jwt")
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class) // Global exception handler
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // JWT auth filter
                .authorizeHttpRequests(requests -> requests.anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public SecurityFilterChain isUserBannedFilterChain(HttpSecurity http, ExceptionHandlerFilter exceptionHandlerFilter, JwtFilter jwtFilter, IsUserBannedFilter isUserBannedFilter) throws Exception {
        CommonSecurityConfig.disableCommonFilters(http);
        http
                .securityMatcher("/test/isUserBanned")
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class) // Global exception handler
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // JWT auth filter
                .addFilterAfter(isUserBannedFilter, SecurityContextHolderAwareRequestFilter.class) // should be applied only after this filter as .getPrincipal() is used and it's configured in the following filter
                .authorizeHttpRequests(requests -> requests.anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public SecurityFilterChain originCheckFilterChain(HttpSecurity http, ExceptionHandlerFilter exceptionHandlerFilter, OriginCheckFilter originCheckFilter) throws Exception {
        CommonSecurityConfig.disableCommonFilters(http);
        http
                .securityMatcher("/test/originCheck")
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class) // Global exception handler
                .addFilterBefore(originCheckFilter, UsernamePasswordAuthenticationFilter.class ) // Validates request's origin to protect from CSRF attacks
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        return http.build();
    }

}