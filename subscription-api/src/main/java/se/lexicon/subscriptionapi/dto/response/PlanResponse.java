package se.lexicon.subscriptionapi.dto.response;

import java.math.BigDecimal;

import se.lexicon.subscriptionapi.domain.constant.NetworkGeneration;
import se.lexicon.subscriptionapi.domain.constant.PlanKind;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public record PlanResponse(
        Long id,
        PlanKind kind,
        String name,
        OperatorResponse operator,
        BigDecimal price,
        PlanStatus status,
        // Internet-specific
        Integer uploadSpeedMbps,
        Integer downloadSpeedMbps,
        // Cellular-specific
        NetworkGeneration networkGeneration,
        Integer dataLimitGb,
        BigDecimal callCostPerMinute,
        BigDecimal smsCostPerMessage,
        // Satellite-specific
        String coverage,
        String frequencyBand
) {}
