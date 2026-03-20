package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.*;
import se.lexicon.subscriptionapi.domain.constant.RequestStatus;
import se.lexicon.subscriptionapi.domain.entity.ChangeRequest;
import se.lexicon.subscriptionapi.domain.entity.request.*;
import se.lexicon.subscriptionapi.domain.entity.user.UserAdmin;
import se.lexicon.subscriptionapi.domain.entity.user.UserOperator;
import se.lexicon.subscriptionapi.dto.request.*;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.ChangeRequestResponse;


@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestedBy", source = "operator")
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "request_instant", ignore = true)
    @Mapping(target = "review_instant", ignore = true)
    @Mapping(target = "actionType", ignore = true)
    @Mapping(target = "planKind", source = "dto.kind")
    @Mapping(target = "planName", source = "dto.name")
    @Mapping(target = "planPrice", source = "dto.price")
    @Mapping(target = "planStatus", source = "dto.status")
    CreatePlanRequest toCreatePlanRequest(CreatePlanChangeRequest dto, UserOperator operator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestedBy", source = "operator")
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "request_instant", ignore = true)
    @Mapping(target = "review_instant", ignore = true)
    @Mapping(target = "actionType", ignore = true)
    @Mapping(target = "targetPlanId", source = "dto.targetPlanId")
    @Mapping(target = "planKind", source = "dto.kind")
    @Mapping(target = "planName", source = "dto.name")
    @Mapping(target = "planPrice", source = "dto.price")
    @Mapping(target = "planStatus", source = "dto.status")
    UpdatePlanRequest toUpdatePlanRequest(UpdatePlanChangeRequest dto, UserOperator operator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestedBy", source = "operator")
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "request_instant", ignore = true)
    @Mapping(target = "review_instant", ignore = true)
    @Mapping(target = "actionType", ignore = true)
    DeletePlanRequest toDeletePlanRequest(DeletePlanChangeRequest dto, UserOperator operator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestedBy", source = "operator")
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "request_instant", ignore = true)
    @Mapping(target = "review_instant", ignore = true)
    @Mapping(target = "actionType", ignore = true)
    @Mapping(target = "targetOperatorId", source = "dto.targetOperatorId")
    @Mapping(target = "newOperatorName", source = "dto.newName")
    UpdateOperatorRequest toUpdateOperatorRequest(UpdateOperatorChangeRequest dto, UserOperator operator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestedBy", source = "operator")
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "request_instant", ignore = true)
    @Mapping(target = "review_instant", ignore = true)
    @Mapping(target = "actionType", ignore = true)
    CreateSubscriptionRequest toCreateSubscriptionRequest(CreateSubscriptionChangeRequest dto, UserOperator operator);

    @Mapping(target = "kind", source = "planKind")
    @Mapping(target = "name", source = "planName")
    @Mapping(target = "operator", source = "requestedBy.operator.id")
    @Mapping(target = "price", source = "planPrice")
    @Mapping(target = "status", source = "planStatus")
    PlanRequest toPlanRequest(CreatePlanRequest request);

    @Mapping(target = "kind", source = "planKind")
    @Mapping(target = "name", source = "planName")
    @Mapping(target = "operator", source = "requestedBy.operator.id")
    @Mapping(target = "price", source = "planPrice")
    @Mapping(target = "status", source = "planStatus")
    PlanRequest toPlanRequest(UpdatePlanRequest request);

    @Mapping(target = "operator", source = "targetOperatorId")
    @Mapping(target = "name", source = "newOperatorName")
    OperatorRequest toOperatorRequest(UpdateOperatorRequest request);

    @Mapping(target = "operatorId", source = "requestedBy.operator.id")
    @Mapping(target = "planId", source = "targetPlanId")
    @Mapping(target = "userId", source = "targetUserId")
    @Mapping(target = "status", constant = "ACTIVE")
    SubscriptionRequest toSubscriptionRequest(CreateSubscriptionRequest request);

    default ChangeRequest toApproved(ChangeRequest request, UserAdmin admin) {
        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedBy(admin);
        return request;
    }

    default ChangeRequest toRejected(ChangeRequest request, String reason, UserAdmin admin) {
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        request.setReviewedBy(admin);
        return request;
    }

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @SubclassMapping(source = CreatePlanRequest.class, target = ChangeRequestResponse.class)
    @SubclassMapping(source = UpdatePlanRequest.class, target = ChangeRequestResponse.class)
    @SubclassMapping(source = DeletePlanRequest.class, target = ChangeRequestResponse.class)
    @SubclassMapping(source = UpdateOperatorRequest.class, target = ChangeRequestResponse.class)
    @SubclassMapping(source = CreateSubscriptionRequest.class, target = ChangeRequestResponse.class)
    ChangeRequestResponse toResponse(ChangeRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestedById", source = "requestedBy.id")
    @Mapping(target = "requestedByEmail", source = "requestedBy.email")
    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    @Mapping(target = "reviewedByEmail", source = "reviewedBy.email")
    ChangeRequestResponse toResponse(CreatePlanRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestedById", source = "requestedBy.id")
    @Mapping(target = "requestedByEmail", source = "requestedBy.email")
    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    @Mapping(target = "reviewedByEmail", source = "reviewedBy.email")
    ChangeRequestResponse toResponse(UpdatePlanRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestedById", source = "requestedBy.id")
    @Mapping(target = "requestedByEmail", source = "requestedBy.email")
    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    @Mapping(target = "reviewedByEmail", source = "reviewedBy.email")
    ChangeRequestResponse toResponse(DeletePlanRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestedById", source = "requestedBy.id")
    @Mapping(target = "requestedByEmail", source = "requestedBy.email")
    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    @Mapping(target = "reviewedByEmail", source = "reviewedBy.email")
    ChangeRequestResponse toResponse(UpdateOperatorRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "requestedById", source = "requestedBy.id")
    @Mapping(target = "requestedByEmail", source = "requestedBy.email")
    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    @Mapping(target = "reviewedByEmail", source = "reviewedBy.email")
    ChangeRequestResponse toResponse(CreateSubscriptionRequest request);
}
