package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import se.lexicon.subscriptionapi.domain.entity.CustomerDetail;
import se.lexicon.subscriptionapi.dto.request.CustomerDetailRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerDetailResponse;

@Mapper(componentModel = "spring")
public interface CustomerDetailMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customer", ignore = true)
    CustomerDetail toEntity(CustomerDetailRequest request);

    CustomerDetailResponse toResponse(CustomerDetail customerDetail);
}
