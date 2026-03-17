package se.lexicon.subscriptionapi.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;

public record SubscriptionResponse(
        Long id,
        Long customerId,
        Long planId,
        String planName,
        BigDecimal planPrice,
        ServiceType serviceType,
        SubscriptionStatus status,
        Instant subscribedAt,
        Instant cancelledAt
) {}
