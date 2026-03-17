package se.lexicon.subscriptionapi.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.PlanCellular;
import se.lexicon.subscriptionapi.domain.entity.PlanInternet;


@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByNameIgnoreCase(String name);
    List<Plan> findAllByOperatorNameIgnoreCase(String operatorName);
    List<Plan> findByOperatorId(Long operatorId);
    List<Plan> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Plan> findByStatus(PlanStatus status);
    boolean existsByOperatorId(Long operatorId);
    long countByOperatorIdAndStatus(Long operatorId, PlanStatus status);

    @Query("SELECT p FROM PlanInternet p")
    List<PlanInternet> findAllInternetPlans();

    @Query("SELECT p FROM PlanCellular p")
    List<PlanCellular> findAllCellularPlans();
}
