package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.PlanCellular;
import se.lexicon.subscriptionapi.domain.entity.PlanInternet;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", ignore = true)
    PlanInternet toInternetEntity(PlanRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", ignore = true)
    PlanCellular toCellularEntity(PlanRequest request);

    @Mapping(target = "networkGeneration", ignore = true)
    @Mapping(target = "callCostPerMinute", ignore = true)
    @Mapping(target = "smsCostPerMessage", ignore = true)
    PlanResponse toResponse(PlanInternet plan);

    @Mapping(target = "uploadSpeedMbps", ignore = true)
    @Mapping(target = "downloadSpeedMbps", ignore = true)
    PlanResponse toResponse(PlanCellular plan);

    @SubclassMapping(source = PlanInternet.class, target = PlanResponse.class)
    @SubclassMapping(source = PlanCellular.class, target = PlanResponse.class)
    @Mapping(target = "uploadSpeedMbps", ignore = true)
    @Mapping(target = "downloadSpeedMbps", ignore = true)
    @Mapping(target = "networkGeneration", ignore = true)
    @Mapping(target = "callCostPerMinute", ignore = true)
    @Mapping(target = "smsCostPerMessage", ignore = true)
    PlanResponse toResponse(Plan plan);
}
