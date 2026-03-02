package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;

@Mapper(componentModel = "spring")
public interface OperatorMapper {

    Operator toEntity(OperatorRequest request);

    OperatorResponse toResponse(Operator operator);
}
