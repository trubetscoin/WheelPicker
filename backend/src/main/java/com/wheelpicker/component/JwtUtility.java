package com.wheelpicker.component;

import com.wheelpicker.dto.JwtWithExpiryDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtUtility {

    private final String accessSecretKey;
    private final Long accessSecretExpiry;
    private final String refreshSecretKey;
    private final Long refreshSecretExpiry;

    public JwtUtility(@Value("${jwtUtility.accessSecretKey}") String accessSecretKey,
                      @Value("${jwtUtility.accessTokenExpiryMs}") Long accessSecretExpiry,
                      @Value("${jwtUtility.refreshSecretKey}") String refreshSecretKey,
                      @Value("${jwtUtility.refreshTokenExpiryMs}") Long refreshSecretExpiry)
    {
        this.accessSecretKey = accessSecretKey;
        this.accessSecretExpiry = accessSecretExpiry;
        this.refreshSecretKey = refreshSecretKey;
        this.refreshSecretExpiry = refreshSecretExpiry;
    }

    public String generateAccessToken(String email) {
        Date expiryDate = new Date(System.currentTimeMillis() + accessSecretExpiry);
        return generateToken(email, expiryDate, accessSecretKey);
    }

    public JwtWithExpiryDto generateRefreshToken(String email) {
        Date expiryDate = new Date(System.currentTimeMillis() + refreshSecretExpiry);
        LocalDateTime localExpiry = new java.sql.Timestamp(expiryDate.getTime()).toLocalDateTime();
        String token = generateToken(email, expiryDate, refreshSecretKey);
        return new JwtWithExpiryDto(token, localExpiry);
    }

    private String generateToken(String email, Date expiryDate, String secret) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAccessClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(accessSecretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(refreshSecretKey))
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String email = claims.getSubject();
        return generateAccessToken(email);
    }

    private Key getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
