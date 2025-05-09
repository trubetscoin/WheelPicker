package com.wheel.wheelPicker.dto;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class JwtWithExpiryDto {
    private final String token;
    private final LocalDateTime expiryDate;

    public JwtWithExpiryDto(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
