package com.wheelpicker.component;

import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.model.RefreshToken;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtUtility {
    private final RefreshTokenRepository refreshTokenRepository;

    private final String ACCESS_SECRET_KEY;
    private final String REFRESH_SECRET_KEY;
    private final Long ACCESS_SECRET_EXPIRY = 1000L * 60 * 15; // 15 minutes
    private final Long REFRESH_SECRET_EXPIRY = 1000L * 60 * 60; // 1 hour

    public JwtUtility(RefreshTokenRepository refreshTokenRepository, @Value("${jwtUtility.accessTokenSecret}") String accessTokenSecret, @Value("${jwtUtility.refreshTokenSecret}") String refreshTokenSecret) {
        this.refreshTokenRepository = refreshTokenRepository;
        ACCESS_SECRET_KEY = accessTokenSecret;
        REFRESH_SECRET_KEY = refreshTokenSecret;
    }

    public String generateAccessToken(String email) {
        Date expiryDate = new Date(System.currentTimeMillis() + ACCESS_SECRET_EXPIRY);
        return generateToken(email, expiryDate, ACCESS_SECRET_KEY);
    }

    public JwtWithExpiryDto generateRefreshToken(String email) {
        Date expiryDate = new Date(System.currentTimeMillis() + REFRESH_SECRET_EXPIRY);
        LocalDateTime localExpiry = new java.sql.Timestamp(expiryDate.getTime()).toLocalDateTime();
        String token = generateToken(email, expiryDate, REFRESH_SECRET_KEY);
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
                .setSigningKey(getSigningKey(ACCESS_SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
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
        refreshTokenRepository.deleteByToken(token);
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(REFRESH_SECRET_KEY))
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
