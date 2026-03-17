package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Entity
@DiscriminatorValue("CELLULAR")
public class PlanCellular extends Plan {
    @Column(name = "network_generation")
    private String networkGeneration;

    @Column(name = "data_limit_gb")
    private Integer dataLimitGb;

    @Column(name = "call_cost_per_minute", precision = 8, scale = 4)
    private BigDecimal callCostPerMinute;

    @Column(name = "sms_cost_per_message", precision = 8, scale = 4)
    private BigDecimal smsCostPerMessage;
}