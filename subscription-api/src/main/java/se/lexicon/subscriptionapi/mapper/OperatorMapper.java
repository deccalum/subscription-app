package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;

@Mapper(componentModel = "spring", uses = PlanMapper.class)
public interface OperatorMapper {
    @Mapping(target = "id", source = "operator")
    @Mapping(target = "plans", ignore = true)
    @Mapping(target = "writeInstant", ignore = true)
    Operator toEntity(OperatorRequest request);

    OperatorResponse toResponse(Operator operator);
}
