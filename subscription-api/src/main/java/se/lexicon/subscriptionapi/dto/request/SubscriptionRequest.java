package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;

public record SubscriptionRequest(
        @NotNull(message = "{required}") 
        @Positive(message = "{invalidId}")
        Long operatorId,

        @NotNull(message = "{required}") 
        @Positive(message = "{invalidId}") 
        Long planId,

        @NotNull(message = "{required}") 
        @Positive(message = "{invalidId}") 
        Long customerId,

        @NotNull(message = "{required}") 
        SubscriptionStatus status
) {}
