package se.lexicon.subscriptionapi.domain.entity.plan;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import se.lexicon.subscriptionapi.domain.constant.NetworkGeneration;
import se.lexicon.subscriptionapi.domain.entity.Plan;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@DiscriminatorValue("CELLULAR")
public class PlanCellular extends Plan {
    @Enumerated(EnumType.STRING)
    @Column(name = "network_generation")
    private NetworkGeneration networkGeneration;

    @Column(name = "data_limit_gb")
    private Integer dataLimitGb;

    @Column(name = "call_cost_per_minute")
    private BigDecimal callCostPerMinute;

    @Column(name = "sms_cost_per_message")
    private BigDecimal smsCostPerMessage;
}
