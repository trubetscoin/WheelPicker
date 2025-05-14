package com.wheelpicker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class JwtWithExpiryDto {
    private final String token;
    private final LocalDateTime expiryDate;
}