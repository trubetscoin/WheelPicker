package com.wheelpicker.unit;

import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtUtilityTest {

    private static JwtUtility jwtUtility;

    @BeforeAll
    static void setup() {
        String accessSecret = "QSBzdHJpbmcgZm9yIGFjY2Vzc1Rva2VuIHRvIHRlc3Qgand0VXRpbGl0eSBjb21wb25lbnQgZm9yIGEgV2hlZWxQaWNrZXIgQXBwbGljYXRpb24=";
        String refreshSecret = "QSBzdHJpbmcgZm9yIHJlZnJlc2hUb2tlbiB0byB0ZXN0IGp3dFV0aWxpdHkgY29tcG9uZW50IGZvciBhIFdoZWVsUGlja2VyIEFwcGxpY2F0aW9u";
        Long accessSecretExpiry = 1000L * 60 *  15;
        Long refreshSecretExpiry = 1000L * 60 *  15;

        jwtUtility = new JwtUtility(accessSecret, accessSecretExpiry, refreshSecret, refreshSecretExpiry);
    }

    @Test
    void generateAccessToken_shouldGenerate() {
        String email = "test@example.com";

        String token = jwtUtility.generateAccessToken(email);
        assertNotNull(token);

        Claims claims = jwtUtility.extractAccessClaims(token);
        assertEquals(email, claims.getSubject());
    }

    @Test
    void generateRefreshToken_shouldGenerate() {
        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken("test@example.com");
        assertNotNull(refreshToken);
    }

    @Test
    void refreshAccessToken_shouldRefresh() {
        User user = new User();
        user.setEmail("test@example.com");

        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken(user.getEmail());

        String newAccessToken = jwtUtility.refreshAccessToken(refreshToken.getToken());

        Claims claims = jwtUtility.extractAccessClaims(newAccessToken);

        assertEquals(user.getEmail(), claims.getSubject());
    }

}
