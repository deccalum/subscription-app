package se.lexicon.subscriptionapi.domain.constant;

import java.util.function.Supplier;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.PlanCellular;
import se.lexicon.subscriptionapi.domain.entity.PlanInternet;
import se.lexicon.subscriptionapi.domain.entity.PlanSatellite;

public enum PlanKind {
    INTERNET(PlanInternet::new),
    CELLULAR(PlanCellular::new),
    SATELLITE(PlanSatellite::new);

    private final Supplier<Plan> factory;
    PlanKind(Supplier<Plan> factory) { this.factory = factory; }
    public Plan create() { return factory.get(); }
}