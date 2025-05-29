package com.wheelpicker.unit;

import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.model.RefreshToken;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.RefreshTokenRepository;
import com.wheelpicker.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void getRefreshTokenFromDb_shouldThrowTokenIsNull() {
        assertThrows(BadCredentialsException.class, () -> refreshTokenService.getRefreshTokenFromDb(null));
    }

    @Test
    void getRefreshTokenFromDb_shouldThrowTokenNotFound() {
        String token = "nonexistentToken";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> refreshTokenService.getRefreshTokenFromDb(token));
    }

    @Test
    void getRefreshTokenFromDb_shouldReturnRefreshToken() {
        String token = "validToken";
        User user = new User();
        user.setEmail("test@example.com");

        RefreshToken refreshToken = new RefreshToken(token, LocalDateTime.now().plusHours(1), user);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshToken retrievedTokenEntity = refreshTokenService.getRefreshTokenFromDb(token);
        assertEquals(token, retrievedTokenEntity.getToken());
        assertEquals(user, retrievedTokenEntity.getUser());
    }

    @Test
    void saveRefreshToken_shouldSave() {
        JwtWithExpiryDto dto = new JwtWithExpiryDto("tokenValue", LocalDateTime.now().plusHours(1));
        User user = new User();

        refreshTokenService.saveRefreshToken(dto, user);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void deleteRefreshToken_shouldDelete() {
        String token = "tokenToDelete";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        refreshTokenService.deleteRefreshToken(token);

        verify(refreshTokenRepository).findByToken(token);
        verify(refreshTokenRepository).deleteByToken(token);
    }

    @Test
    void deleteRefreshToken_shouldThrowTokenNotFound() {
        String token = "nonexistentToken";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> refreshTokenService.deleteRefreshToken(token));
        verify(refreshTokenRepository, never()).deleteByToken(anyString());
    }
}

