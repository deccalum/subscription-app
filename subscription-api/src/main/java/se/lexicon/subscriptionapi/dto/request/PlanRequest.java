package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public record PlanRequest(
        @NotBlank(message = "{blank}") 
        String kind,

        @NotBlank(message = "{blank}") 
        String name,

        @NotNull(message = "{required}") 
        Long operatorId,

        @NotNull(message = "{required}") 
        @Positive(message = "{positive}") 
        @Digits(integer = 10, fraction = 2, message = "{digits}") 
        BigDecimal price,

        @NotNull(message = "{required}")
        PlanStatus status,

        Integer uploadSpeedMbps,
        Integer downloadSpeedMbps,

        String networkGeneration,
        Integer dataLimitGb,
        BigDecimal callCostPerMinute,
        BigDecimal smsCostPerMessage
) {}
