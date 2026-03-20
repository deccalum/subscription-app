package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import java.math.BigDecimal;
import java.util.List;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public interface PlanService {
    PlanResponse create(PlanRequest request);
    PlanResponse update(Long id, PlanRequest request);
    PlanResponse read(Long id);
    void delete(Long id);

    PlanResponse getByName(String name);
    List<PlanResponse> findByOperatorName(String operatorName);
    List<PlanResponse> findByOperatorId(Long operatorId);
    List<PlanResponse> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<PlanResponse> findByStatus(PlanStatus status);
    boolean existsByOperatorId(Long operatorId);
    long countByOperatorIdAndStatus(Long operatorId, PlanStatus status);
    List<PlanResponse> findAllInternetPlans();
    List<PlanResponse> findAllCellularPlans();
}
