package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;

public interface PlanService {
    PlanResponse create(PlanRequest request);
    PlanResponse update(Long id, PlanRequest request);
    PlanResponse read(Long id);
    void delete(Long id);
}
