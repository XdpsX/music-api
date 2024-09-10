package com.xdpsx.music.schedule;

import com.xdpsx.music.repository.ConfirmTokenRepository;
import com.xdpsx.music.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenRemovalScheduledTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRemovalScheduledTask.class);

    private final ConfirmTokenRepository confirmTokenRepository;
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void deleteExpiredTokens() {
        int tokensDeleted = confirmTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        LOGGER.info("Number of expired confirm tokens deleted: " + tokensDeleted);
    }

    @Scheduled(cron = "0 0 0 */1 * ?")
    public void deleteRevokedTokens() {
        int tokensDeleted = tokenRepository.deleteRevokedTokens();
        LOGGER.info("Number of revoked tokens deleted: " + tokensDeleted);
    }
}
