package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import se.lexicon.subscriptionapi.domain.constant.NetworkGeneration;
import se.lexicon.subscriptionapi.domain.constant.PlanKind;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public record PlanRequest(
        @NotNull(message = "{required}") 
        PlanKind kind,

        @NotNull(message = "{required}") 
        String name,

        @NotNull(message = "{required}") 
        Long operator,

        @NotNull(message = "{required}") 
        @Positive(message = "{positive}") 
        @Digits(integer = 10, fraction = 2, message = "{digits}") 
        BigDecimal price,

        @NotNull(message = "{required}")
        PlanStatus status,

        @Positive
        @Digits(integer = 5, fraction = 0, message = "{digits}")
        Integer uploadSpeedMbps,

        @Positive
        @Digits(integer = 5, fraction = 0, message = "{digits}")
        Integer downloadSpeedMbps,

        // NetworkGeneration is an enum — Jackson rejects invalid values at deserialization, null is still accepted (optional)
        NetworkGeneration networkGeneration,

        @Positive
        @Digits(integer = 5, fraction = 2, message = "{digits}")
        Integer dataLimitGb,

        @Positive
        @Digits(integer = 8, fraction = 4, message = "{digits}")
        BigDecimal callCostPerMinute,

        @Positive
        @Digits(integer = 8, fraction = 4, message = "{digits}")
        BigDecimal smsCostPerMessage
) {}
