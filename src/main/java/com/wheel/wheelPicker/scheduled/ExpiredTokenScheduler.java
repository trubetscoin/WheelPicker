package com.wheel.wheelPicker.scheduled;


import com.wheel.wheelPicker.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ExpiredTokenScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    public ExpiredTokenScheduler(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Scheduled(cron = "0 0 */1 * * ?")
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiryDateBefore(now);

    }
}
