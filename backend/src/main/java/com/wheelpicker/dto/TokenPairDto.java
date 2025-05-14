package com.wheelpicker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPairDto {
    private final String accessToken;
    private final JwtWithExpiryDto refreshToken;
}