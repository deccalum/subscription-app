package se.lexicon.subscriptionapi.dto.response;

import java.math.BigDecimal;
import se.lexicon.subscriptionapi.domain.constant.PlanKind;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public record PlanSummaryResponse(Long id, PlanKind kind, String name, BigDecimal price, PlanStatus status) {}