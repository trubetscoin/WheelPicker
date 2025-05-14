package com.wheelpicker.config;

import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class PublicRoutesConfig {
    private static final Set<String> publicRoutes = Set.of(
            "/",
            "/api/auth/*"
    );

    public static String[] getPublicRoutes() {
        return publicRoutes.toArray(new String[0]);
    }
}