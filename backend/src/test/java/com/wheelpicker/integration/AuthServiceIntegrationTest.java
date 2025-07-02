package com.wheelpicker.integration;

import com.wheelpicker.BaseDatabaseTest;
import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.dto.TokenPairDto;
import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.exceptionHandling.exception.CredentialsAlreadyExistsException;
import com.wheelpicker.model.RefreshToken;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import com.wheelpicker.service.AuthService;
import com.wheelpicker.service.RefreshTokenService;
import com.wheelpicker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
public class AuthServiceIntegrationTest extends BaseDatabaseTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_shouldRegisterUser() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        TokenPairDto tokenPairDto = authService.register(registerDto);

        String accessToken = tokenPairDto.getAccessToken();
        String refreshToken = tokenPairDto.getRefreshToken().getToken();
        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshTokenFromDb(refreshToken);
        User registeredUser = refreshTokenEntity.getUser();

        assertEquals("registeruser", registeredUser.getUsername());
        assertEquals("registeruser@example.com", jwtUtility.extractAccessClaims(accessToken).getSubject());
        assertEquals("registeruser@example.com", registeredUser.getEmail());
        assertTrue(passwordEncoder.matches("password123", registeredUser.getPassword()));
        assertTrue(registeredUser.getRoles().contains(Role.USER));

    }

    @Test
    void register_shouldThrowDuplicateEmail() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "user@example.com", "password123");
        UserRegisterDto registerDto2 = new UserRegisterDto("registeruser2", "user@example.com", "password123");

        // no need for generating jwt
        userService.registerUser(registerDto);

        CredentialsAlreadyExistsException e = assertThrows(CredentialsAlreadyExistsException.class, () -> authService.register(registerDto2));
        assertEquals("email", e.getCredentialType());
        assertEquals("user@example.com", e.getCredentialValue());
    }

    @Test
    void register_shouldThrowDuplicateUsername() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "user@example.com", "password123");
        UserRegisterDto registerDto2 = new UserRegisterDto("registeruser", "user2@example.com", "password123");

        // no need for generating jwt
        userService.registerUser(registerDto);

        CredentialsAlreadyExistsException e = assertThrows(CredentialsAlreadyExistsException.class, () -> authService.register(registerDto2));
        assertEquals("username", e.getCredentialType());
        assertEquals("registeruser", e.getCredentialValue());
    }

    @Test
    void login_shouldLoginUser() {
        UserRegisterDto registerDto = new UserRegisterDto("user", "user@example.com", "password123");
        UserLoginDto loginDto = new UserLoginDto("user@example.com", "password123");

        // no need for generating jwt
        userService.registerUser(registerDto);

        TokenPairDto tokenPairDtoLogin = authService.login(loginDto);

        String accessToken = tokenPairDtoLogin.getAccessToken();
        String refreshToken = tokenPairDtoLogin.getRefreshToken().getToken();
        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshTokenFromDb(refreshToken);
        User loggedUser = refreshTokenEntity.getUser();

        assertEquals("user", loggedUser.getUsername());
        assertEquals("user@example.com", jwtUtility.extractAccessClaims(accessToken).getSubject());
        assertEquals("user@example.com", loggedUser.getEmail());
    }

    @Test
    void getNewAccessToken_shouldGiveNewAccessToken() throws InterruptedException {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        TokenPairDto tokenPairDto = authService.register(registerDto);
        String accessToken = tokenPairDto.getAccessToken();
        String refreshToken = tokenPairDto.getRefreshToken().getToken();

        Thread.sleep(1000); // Needed as locally 0 ms delay results in generating the very same token
        String newAccessToken = authService.getNewAccessToken(refreshToken);

        assertNotEquals(accessToken, newAccessToken);
        assertEquals("registeruser@example.com", jwtUtility.extractAccessClaims(accessToken).getSubject());
        assertEquals("registeruser@example.com", jwtUtility.extractAccessClaims(newAccessToken).getSubject());
    }

    @Test
    void logout_shouldLogoutUser() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        TokenPairDto tokenPairDto = authService.register(registerDto);
        String accessToken = tokenPairDto.getAccessToken();
        String refreshToken = tokenPairDto.getRefreshToken().getToken();

        authService.logout(refreshToken);

        assertEquals("registeruser@example.com", jwtUtility.extractAccessClaims(accessToken).getSubject());
        assertThrows(BadCredentialsException.class, () -> refreshTokenService.getRefreshTokenFromDb(refreshToken));
    }

    @Test
    void logout_shouldThrowNoTokenFoundToDelete() {
        String refreshToken = "malformed token";

        assertThrows(BadCredentialsException.class, () -> authService.logout(refreshToken));
    }
}
