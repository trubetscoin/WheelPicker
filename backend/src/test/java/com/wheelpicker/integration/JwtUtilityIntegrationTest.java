package com.wheelpicker.integration;

import com.wheelpicker.BaseDatabaseTest;
import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.model.RefreshToken;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
public class JwtUtilityIntegrationTest extends BaseDatabaseTest{

    private final JwtUtility jwtUtility;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public JwtUtilityIntegrationTest(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Test
    void shouldGenerateAccessTokenAndExtractClaims() {
        String email = "test@example.com";

        String token = jwtUtility.generateAccessToken(email);
        assertNotNull(token);

        Claims claims = jwtUtility.extractAccessClaims(token);
        assertEquals(email, claims.getSubject());
    }

    @Test
    void shouldGenerateAndSaveRefreshToken() {
        User user = new User();
        user.setEmail("test@example.com");

        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken(user.getEmail());

        // Simulate saving the refresh token to DB
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(new RefreshToken(refreshToken.getToken(), refreshToken.getExpiryDate(), user));

        jwtUtility.saveRefreshToken(refreshToken, user);

        // Simulate fetching the refresh token from DB
        RefreshToken simulatedRefreshTokenEntity = new RefreshToken(refreshToken.getToken(), refreshToken.getExpiryDate(), user);
        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(simulatedRefreshTokenEntity));

        RefreshToken retrievedRefreshTokenEntity = jwtUtility.getRefreshTokenFromDb(refreshToken.getToken());

        assertEquals("test@example.com", retrievedRefreshTokenEntity.getUser().getEmail());
        assertEquals(refreshToken.getToken(), retrievedRefreshTokenEntity.getToken());
    }

    @Test
    void shouldDeleteRefreshToken() {
        User user = new User();
        user.setEmail("test@example.com");

        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken(user.getEmail());
        jwtUtility.saveRefreshToken(refreshToken, user);

        jwtUtility.deleteRefreshToken(refreshToken.getToken());

        assertThrows(BadCredentialsException.class, () -> jwtUtility.getRefreshTokenFromDb(refreshToken.getToken()));
    }

    @Test
    void shouldNotFindRefreshToken() {
        assertThrows(BadCredentialsException.class, () -> jwtUtility.getRefreshTokenFromDb(null));
        assertThrows(BadCredentialsException.class, () -> jwtUtility.getRefreshTokenFromDb("Some non-existent token"));
    }

}
