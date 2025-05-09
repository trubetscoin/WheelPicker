package com.wheel.wheelPicker.config;

import com.wheel.wheelPicker.security.filter.IsUserBannedFilter;
import com.wheel.wheelPicker.security.filter.JwtFilter;
import com.wheel.wheelPicker.security.filter.OriginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Needed for the filters not to be added into global spring filters registrations but into SecurityConfig
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<OriginCheckFilter> originCheckFilterFilterRegistrationBean(OriginCheckFilter originCheckFilter) {
        FilterRegistrationBean<OriginCheckFilter> registration = new FilterRegistrationBean<>(originCheckFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<IsUserBannedFilter> isUserBannedFilterRegistrationBean(IsUserBannedFilter isUserBannedFilter) {
        FilterRegistrationBean<IsUserBannedFilter> registration = new FilterRegistrationBean<>(isUserBannedFilter);
        registration.setEnabled(false);
        return registration;
    }
}