package se.lexicon.subscriptionapi.dto.response;

import java.math.BigDecimal;

public record PlanResponse(
        Long id,
        String name,
        BigDecimal price,
        String status,
        // Internet
        Integer uploadSpeedMbps,
        Integer downloadSpeedMbps,
        // Cellular
        String networkGeneration,
        Integer dataLimitGb,
        BigDecimal callCostPerMinute,
        BigDecimal smsCostPerMessage
) {}
