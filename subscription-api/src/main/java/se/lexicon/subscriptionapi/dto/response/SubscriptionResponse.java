package se.lexicon.subscriptionapi.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;

public record SubscriptionResponse(
        Long id,
        Long operatorId,
        Long userId,
        Long planId,
        String planName,
        BigDecimal planPrice,
        SubscriptionStatus status,
        Instant writeInstant,
        Instant cancelInstant
) {}
