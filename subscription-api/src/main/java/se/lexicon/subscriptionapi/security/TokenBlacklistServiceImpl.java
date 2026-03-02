package se.lexicon.subscriptionapi.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.security.token-blacklist.enabled:true}")
    private boolean isEnabled;

    private static final String BLACKLIST_PREFIX = "jwt_blacklist:";

    @Override
    public void blacklistToken(String token, Duration expiration) {
        if (!isEnabled) {
            log.info("Token blacklisting is disabled. Skipping Redis storage.");
            return;
        }
        log.info("Blacklisting token with prefix: {}", BLACKLIST_PREFIX);
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", expiration);
        log.info("Token successfully stored in Redis with TTL: {}", expiration);
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (!isEnabled) {
            return false;
        }
        boolean blacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        if (blacklisted) {
            log.info("Token found in blacklist");
        }
        return blacklisted;
    }
}
