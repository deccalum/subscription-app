package se.lexicon.subscriptionapi.domain.entity.plan;

import jakarta.persistence.*;
import lombok.*;
import se.lexicon.subscriptionapi.domain.entity.Plan;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@DiscriminatorValue("SATELLITE")
public class PlanSatellite extends Plan {
    @Column(name = "coverage")
    private String coverage;

    @Column(name = "frequency_band")
    private String frequencyBand;
}
