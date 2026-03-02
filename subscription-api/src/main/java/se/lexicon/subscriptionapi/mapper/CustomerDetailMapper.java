package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import se.lexicon.subscriptionapi.domain.entity.CustomerDetail;
import se.lexicon.subscriptionapi.dto.request.CustomerDetailRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerDetailResponse;

@Mapper(componentModel = "spring")
public interface CustomerDetailMapper {

    CustomerDetail toEntity(CustomerDetailRequest request);

    CustomerDetailResponse toResponse(CustomerDetail customerDetail);
}
