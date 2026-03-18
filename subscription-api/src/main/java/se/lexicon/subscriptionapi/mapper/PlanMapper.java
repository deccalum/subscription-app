package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassMapping;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.PlanCellular;
import se.lexicon.subscriptionapi.domain.entity.PlanInternet;
import se.lexicon.subscriptionapi.domain.entity.PlanSatellite;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    /**
     * The Dispatcher: Routes the generic Plan to the specific subclass mapper.
     * This bypasses the SubclassMapping limitation for updates.
     */
    default Plan toEntity(PlanRequest request, @MappingTarget Plan plan, Operator operator) {
        if (plan instanceof PlanInternet internet) return updateInternet(request, internet, operator);
        if (plan instanceof PlanCellular cellular) return updateCellular(request, cellular, operator);
        if (plan instanceof PlanSatellite satellite) return updateSatellite(request, satellite, operator);
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
    PlanSatellite updateSatellite(PlanRequest request, @MappingTarget PlanSatellite plan, Operator operator);

    /**
     * Converts a Plan entity to a PlanResponse DTO.
     * This method uses subclass mappings to handle different Plan types.
     * @param plan The Plan entity to be converted.
     * @return The corresponding PlanResponse DTO.
     */
    @SubclassMapping(source = PlanInternet.class, target = PlanResponse.class)
    @SubclassMapping(source = PlanCellular.class, target = PlanResponse.class)
    @SubclassMapping(source = PlanSatellite.class, target = PlanResponse.class)
    PlanResponse toResponse(Plan plan);
}
