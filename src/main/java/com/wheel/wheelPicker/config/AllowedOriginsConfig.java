package com.wheel.wheelPicker.config;

import java.util.List;
import java.util.Set;

public class AllowedOriginsConfig {
    private static final Set<String> allowedOrigins = Set.of(
            "http://localhost:3000"
    );

    public static List<String> getAllowedOrigins() {
        return allowedOrigins.stream().toList();
    }
}
