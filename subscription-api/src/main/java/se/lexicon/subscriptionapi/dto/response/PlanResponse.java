package se.lexicon.subscriptionapi.dto.response;

import java.math.BigDecimal;

import se.lexicon.subscriptionapi.domain.constant.PlanKind;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public record PlanResponse(
        Long id,
        PlanKind kind,
        String name,
        OperatorResponse operator,
        BigDecimal price,
        PlanStatus status,
        // Internet
        Integer uploadSpeedMbps,
        Integer downloadSpeedMbps,
        // Cellular
        String networkGeneration,
        Integer dataLimitGb,
        BigDecimal callCostPerMinute,
        BigDecimal smsCostPerMessage
) {}
