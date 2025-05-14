package com.wheelpicker.repository;

import com.wheelpicker.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime now);
    void deleteByToken(String token);
}
