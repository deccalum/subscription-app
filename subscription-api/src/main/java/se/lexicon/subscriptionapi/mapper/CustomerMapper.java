package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "customerDetail", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    Customer toEntity(CustomerRequest request);

    @Mapping(target = "id", source = "customer.id")
    CustomerResponse toResponse(Customer customer);
}
