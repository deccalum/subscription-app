package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "customerDetail", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    Customer toEntity(CustomerRequest request);

    CustomerResponse toResponse(Customer customer);
}
