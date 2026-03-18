package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", source = "plan")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "subscribedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    Subscription toEntity(SubscriptionRequest request, Plan plan, Customer customer);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planName", source = "plan.name")
    @Mapping(target = "planPrice", source = "plan.price")
    @Mapping(target = "operatorId", source = "plan.operator.id")
    SubscriptionResponse toResponse(Subscription subscription);
}
