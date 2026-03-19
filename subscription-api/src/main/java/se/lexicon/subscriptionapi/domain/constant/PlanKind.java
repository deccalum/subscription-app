package se.lexicon.subscriptionapi.domain.constant;

import java.util.function.Supplier;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.plan.PlanCellular;
import se.lexicon.subscriptionapi.domain.entity.plan.PlanInternet;
import se.lexicon.subscriptionapi.domain.entity.plan.PlanSatellite;

public enum PlanKind {
    INTERNET(PlanInternet::new),
    CELLULAR(PlanCellular::new),
    SATELLITE(PlanSatellite::new);

    private final Supplier<Plan> factory;
    PlanKind(Supplier<Plan> factory) {
        this.factory = factory;
    }
    public Plan create() {
        return factory.get();
    }
}