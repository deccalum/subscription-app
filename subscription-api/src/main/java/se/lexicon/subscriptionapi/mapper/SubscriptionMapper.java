package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "subscribedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    Subscription toEntity(SubscriptionRequest request);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planName", source = "plan.name")
    @Mapping(target = "planPrice", source = "plan.price")
    SubscriptionResponse toResponse(Subscription subscription);
}
