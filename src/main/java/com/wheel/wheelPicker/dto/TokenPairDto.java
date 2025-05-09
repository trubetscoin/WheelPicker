package com.wheel.wheelPicker.dto;

import lombok.Getter;

@Getter
public class TokenPairDto {
    private final String accessToken;
    private final JwtWithExpiryDto refreshToken;

    public TokenPairDto(String accessToken, JwtWithExpiryDto refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
