package com.example.flashlearn.config;

import com.example.flashlearn.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TokenCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupTask.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenCleanupTask(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Clean up expired and revoked refresh tokens every 6 hours.
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000, initialDelay = 60 * 1000)
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredAndRevoked(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired/revoked refresh tokens", deleted);
        }
    }
}
