package se.lexicon.subscriptionapi.security;

import java.time.Duration;

public interface TokenBlacklistService {
    void blacklistToken(String token, Duration expiration);
    boolean isBlacklisted(String token);
}
