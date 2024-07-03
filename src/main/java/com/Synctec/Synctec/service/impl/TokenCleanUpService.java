package com.Synctec.Synctec.service.impl;

import com.Synctec.Synctec.domains.VerificationToken;
import com.Synctec.Synctec.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Service
@Slf4j
public class TokenCleanUpService {
    private final TokenRepository tokenRepository;

    // Scheduled to run every day at midnight
//    @Scheduled(cron = "0 0 0 * * ?")
//    @Transactional
//    public void cleanUpExpiredTokens() {
//        // Find tokens where expiration time is before the current time
//        List<VerificationToken> expiredTokens = tokenRepository.findByExpirationTimeBefore(LocalDateTime.now());
//
//        if (!expiredTokens.isEmpty()) {
//            // Remove all expired tokens
//            tokenRepository.deleteAll(expiredTokens);
//            System.out.println("Cleaned up " + expiredTokens.size() + " expired tokens.");
//        }
//    }

}
