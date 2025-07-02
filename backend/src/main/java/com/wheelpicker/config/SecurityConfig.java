package com.wheelpicker.config;

import com.wheelpicker.security.filter.ExceptionHandlerFilter;
import com.wheelpicker.security.filter.IsUserBannedFilter;
import com.wheelpicker.security.filter.JwtFilter;
import com.wheelpicker.security.filter.OriginCheckFilter;
import com.wheelpicker.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    public final CommonSecurityConfig commonSecurityConfig;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, CommonSecurityConfig commonSecurityConfig) {
        this.myUserDetailsService = myUserDetailsService;
        this.commonSecurityConfig = commonSecurityConfig;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http,
                                                 ExceptionHandlerFilter exceptionHandlerFilter) throws Exception {
        commonSecurityConfig.applyCommonFilters(http);

        http
                // Allow access to public pages with no additional checks or filters applied
                .securityMatcher(
                        new OrRequestMatcher(
                                Arrays.stream(PublicRoutesConfig.getPublicRoutes())
                                .map(route -> new AntPathRequestMatcher(route))
                                .map(matcher -> (RequestMatcher) matcher)
                                .toList()
                        )
                )
                .securityContext(context -> context.disable())
                .servletApi(api -> api.disable())
                .headers(headers -> headers.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        return http.build();
    }

    @Profile({"prod", "dev", "integration"})
    @Bean
    @Order(2)
    public SecurityFilterChain protectedFilterChain(HttpSecurity http,
                                                    ExceptionHandlerFilter exceptionHandlerFilter,
                                                    OriginCheckFilter originCheckFilter,
                                                    JwtFilter jwtFilter,
                                                    IsUserBannedFilter isUserBannedFilter) throws Exception {
        commonSecurityConfig.applyCommonFilters(http);
        http
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(originCheckFilter, UsernamePasswordAuthenticationFilter.class ) // Validates request's origin to protect from CSRF attacks
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // JWT auth filter
                .addFilterAfter(isUserBannedFilter, SecurityContextHolderAwareRequestFilter.class) // should be applied only after this filter as .getPrincipal() is used and it's configured in the following filter
                .authorizeHttpRequests(requests -> requests.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // authentication provider for JWT
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}