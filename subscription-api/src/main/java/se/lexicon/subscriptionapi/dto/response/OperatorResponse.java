package se.lexicon.subscriptionapi.dto.response;

import java.util.Set;

public record OperatorResponse(Long id, String name, Set<PlanSummaryResponse> plans) {}
