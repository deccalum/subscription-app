package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import se.lexicon.subscriptionapi.domain.constant.NetworkGeneration;
import se.lexicon.subscriptionapi.domain.constant.PlanKind;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

public record CreatePlanChangeRequest(
        @NotNull(message = "{required}") PlanKind kind,

        @NotBlank(message = "{blank}") String name,

        @NotNull(message = "{required}") @Positive(message = "{positive}") @Digits(integer = 10, fraction = 2, message = "{digits}") BigDecimal price,

        @NotNull(message = "{required}") PlanStatus status,

        @Positive @Digits(integer = 5, fraction = 0, message = "{digits}") Integer uploadSpeedMbps,

        @Positive @Digits(integer = 5, fraction = 0, message = "{digits}") Integer downloadSpeedMbps,

        NetworkGeneration networkGeneration,

        @Positive @Digits(integer = 5, fraction = 2, message = "{digits}") Integer dataLimitGb,

        @Positive @Digits(integer = 8, fraction = 4, message = "{digits}") BigDecimal callCostPerMinute,

        @Positive @Digits(integer = 8, fraction = 4, message = "{digits}") BigDecimal smsCostPerMessage
) {
    @AssertTrue(message = "{invalidPlanPayload}")
    public boolean isPlanPayloadValidForKind() {
        if (kind == null)
            return true;

        return switch (kind) {
            case INTERNET ->
                uploadSpeedMbps != null&& downloadSpeedMbps != null&& networkGeneration == null&& dataLimitGb == null&& callCostPerMinute == null&& smsCostPerMessage == null;
            case CELLULAR ->
                uploadSpeedMbps == null&& downloadSpeedMbps == null&& networkGeneration != null&& dataLimitGb != null&& callCostPerMinute != null&& smsCostPerMessage != null;
            case SATELLITE ->
                uploadSpeedMbps == null&& downloadSpeedMbps == null&& networkGeneration == null&& dataLimitGb == null&& callCostPerMinute == null&& smsCostPerMessage == null;
        };
    }
}
