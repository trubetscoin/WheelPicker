package com.wheelpicker.service;

import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.model.RefreshToken;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.RefreshTokenRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken getRefreshTokenFromDb(String token) {
        if (token == null) throw new BadCredentialsException("Refresh token is missing");
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));
    }

    public void saveRefreshToken(JwtWithExpiryDto token, User user) {
        refreshTokenRepository.save(
                new RefreshToken(
                        token.getToken(),
                        token.getExpiryDate(),
                        user
                )
        );
    }

    public void deleteRefreshToken(String token) {
        this.getRefreshTokenFromDb(token);
        refreshTokenRepository.deleteByToken(token);
    }
}
