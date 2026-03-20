package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.SubclassMapping;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.plan.PlanCellular;
import se.lexicon.subscriptionapi.domain.entity.plan.PlanInternet;
import se.lexicon.subscriptionapi.domain.entity.plan.PlanSatellite;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorSummaryResponse;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.dto.response.PlanSummaryResponse;


@Mapper(componentModel = "spring")
public interface PlanMapper {
    /**
     * The Dispatcher: Routes the generic Plan to the specific subclass mapper.
     * This bypasses the SubclassMapping limitation for updates.
     */
    default Plan toEntity(PlanRequest request, @MappingTarget Plan plan, Operator operator) {
        if (plan instanceof PlanInternet internet)
            return updateInternet(request, internet, operator);
        if (plan instanceof PlanCellular cellular)
            return updateCellular(request, cellular, operator);
        if (plan instanceof PlanSatellite satellite)
            return updateSatellite(request, satellite, operator);
        return plan;
    }

    /**
     * Updates an existing PlanInternet entity with values from the given PlanRequest.
     * @param request The PlanRequest containing the new values.
     * @param plan The existing PlanInternet entity to be updated.
     * @param operator The Operator associated with the plan.
     * @return The updated PlanInternet entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", source = "operator")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "speed.upload", source = "request.uploadSpeedMbps")
    @Mapping(target = "speed.download", source = "request.downloadSpeedMbps")
    PlanInternet updateInternet(PlanRequest request, @MappingTarget PlanInternet plan, Operator operator);

    /**
     * Updates an existing PlanCellular entity with values from the given PlanRequest.
     * @param request The PlanRequest containing the new values.
     * @param plan The existing PlanCellular entity to be updated.
     * @param operator The Operator associated with the plan.
     * @return The updated PlanCellular entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", source = "operator")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "networkGeneration", source = "request.networkGeneration")
    @Mapping(target = "dataLimitGb", source = "request.dataLimitGb")
    @Mapping(target = "callCostPerMinute", source = "request.callCostPerMinute")
    @Mapping(target = "smsCostPerMessage", source = "request.smsCostPerMessage")
    PlanCellular updateCellular(PlanRequest request, @MappingTarget PlanCellular plan, Operator operator);

    /**
     * Updates an existing PlanSatellite entity with values from the given PlanRequest.
     * @param request The PlanRequest containing the new values.
     * @param plan The existing PlanSatellite entity to be updated.
     * @param operator The Operator associated with the plan.
     * @return The updated PlanSatellite entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", source = "operator")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "coverage", ignore = true)
    @Mapping(target = "frequencyBand", ignore = true)
    PlanSatellite updateSatellite(PlanRequest request, @MappingTarget PlanSatellite plan, Operator operator);

    /**
     * Converts a Plan entity to a PlanResponse DTO.
     * Dispatches to the correct subclass method via @SubclassMapping.
     */
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @SubclassMapping(source = PlanInternet.class, target = PlanResponse.class)
    @SubclassMapping(source = PlanCellular.class, target = PlanResponse.class)
    @SubclassMapping(source = PlanSatellite.class, target = PlanResponse.class)
    PlanResponse toResponse(Plan plan);

    @Mapping(target = "uploadSpeedMbps", source = "speed.upload")
    @Mapping(target = "downloadSpeedMbps", source = "speed.download")
    @Mapping(target = "networkGeneration", ignore = true)
    @Mapping(target = "dataLimitGb", ignore = true)
    @Mapping(target = "callCostPerMinute", ignore = true)
    @Mapping(target = "smsCostPerMessage", ignore = true)
    @Mapping(target = "coverage", ignore = true)
    @Mapping(target = "frequencyBand", ignore = true)
    PlanResponse toResponse(PlanInternet plan);

    @Mapping(target = "uploadSpeedMbps", ignore = true)
    @Mapping(target = "downloadSpeedMbps", ignore = true)
    @Mapping(target = "coverage", ignore = true)
    @Mapping(target = "frequencyBand", ignore = true)
    PlanResponse toResponse(PlanCellular plan);

    @Mapping(target = "uploadSpeedMbps", ignore = true)
    @Mapping(target = "downloadSpeedMbps", ignore = true)
    @Mapping(target = "networkGeneration", ignore = true)
    @Mapping(target = "dataLimitGb", ignore = true)
    @Mapping(target = "callCostPerMinute", ignore = true)
    @Mapping(target = "smsCostPerMessage", ignore = true)
    @Mapping(target = "coverage", source = "coverage")
    @Mapping(target = "frequencyBand", source = "frequencyBand")
    PlanResponse toResponse(PlanSatellite plan);

    OperatorSummaryResponse toOperatorSummaryResponse(Operator operator);

    PlanSummaryResponse toSummaryResponse(Plan plan);
}
