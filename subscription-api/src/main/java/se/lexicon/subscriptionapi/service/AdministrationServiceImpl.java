package se.lexicon.subscriptionapi.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.RequestStatus;
import se.lexicon.subscriptionapi.domain.entity.ChangeRequest;
import se.lexicon.subscriptionapi.domain.entity.request.*;
import se.lexicon.subscriptionapi.domain.entity.user.UserAdmin;
import se.lexicon.subscriptionapi.dto.response.ChangeRequestResponse;
import se.lexicon.subscriptionapi.mapper.RequestMapper;
import se.lexicon.subscriptionapi.repository.ChangeRequestRepository;
import se.lexicon.subscriptionapi.repository.UserRepository;

@Service @RequiredArgsConstructor
public class AdministrationServiceImpl implements AdministrationService {
    private final ChangeRequestRepository changeRequestRepository;
    private final UserRepository userRepository;
    private final PlanService planService;
    private final OperatorService operatorService;
    private final SubscriptionService subscriptionService;
    private final RequestMapper requestMapper;

    @Override @Transactional
    public ChangeRequestResponse approveRequest(Long id, String adminEmail) {
        return Optional.ofNullable(id)
                .flatMap(changeRequestRepository::findById)
                .flatMap(this::executeApprovalAction)
                .flatMap(request -> Optional.ofNullable(resolveAdmin(adminEmail)).map(admin -> requestMapper.toApproved(request, admin)))
                .map(changeRequestRepository::save)
                .map(requestMapper::toResponse)
                .orElse(null);
    }

    @Override @Transactional
    public ChangeRequestResponse rejectRequest(Long id, String reason, String adminEmail) {
        return Optional.ofNullable(id)
                .flatMap(changeRequestRepository::findById)
                .flatMap(request -> Optional.ofNullable(resolveAdmin(adminEmail)).map(admin -> requestMapper.toRejected(request, reason, admin)))
                .map(changeRequestRepository::save)
                .map(requestMapper::toResponse)
                .orElse(null);
    }

    @Override @Transactional(readOnly = true)
    public List<ChangeRequestResponse> getByStatus(RequestStatus status) {
        return changeRequestRepository.findByStatus(status).stream().map(requestMapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<ChangeRequestResponse> getPending() {
        return getByStatus(RequestStatus.PENDING);
    }


    // --- PRIVATE UTILITY METHODS ---
    
    private UserAdmin resolveAdmin(String email) {
        return Optional.ofNullable(email).flatMap(userRepository::findByEmail).filter(UserAdmin.class ::isInstance).map(UserAdmin.class ::cast).orElse(null);
    }

    private Optional<ChangeRequest> executeApprovalAction(ChangeRequest request) {
        return executeCreatePlan(request)
                .or(() -> executeUpdatePlan(request))
                .or(() -> executeDeletePlan(request))
                .or(() -> executeUpdateOperator(request))
                .or(() -> executeCreateSubscription(request));
    }

    private Optional<ChangeRequest> executeCreatePlan(ChangeRequest request) {
        return Optional.ofNullable(request)
                .filter(CreatePlanRequest.class ::isInstance)
                .map(CreatePlanRequest.class ::cast)
                .map(requestMapper::toPlanRequest)
                .map(planService::create)
                .map(ignored -> request);
    }

    private Optional<ChangeRequest> executeUpdatePlan(ChangeRequest request) {
        return Optional.ofNullable(request)
                .filter(UpdatePlanRequest.class ::isInstance)
                .map(UpdatePlanRequest.class ::cast)
                .map(r -> planService.update(r.getTargetPlanId(), requestMapper.toPlanRequest(r)))
                .map(ignored -> request);
    }

    private Optional<ChangeRequest> executeDeletePlan(ChangeRequest request) {
        return Optional.ofNullable(request).filter(DeletePlanRequest.class ::isInstance).map(DeletePlanRequest.class ::cast).map(r -> {
            planService.delete(r.getTargetPlanId());
            return request;
        });
    }

    private Optional<ChangeRequest> executeUpdateOperator(ChangeRequest request) {
        return Optional.ofNullable(request)
                .filter(UpdateOperatorRequest.class ::isInstance)
                .map(UpdateOperatorRequest.class ::cast)
                .map(r -> operatorService.update(r.getTargetOperatorId(), requestMapper.toOperatorRequest(r)))
                .map(ignored -> request);
    }

    private Optional<ChangeRequest> executeCreateSubscription(ChangeRequest request) {
        return Optional.ofNullable(request)
                .filter(CreateSubscriptionRequest.class ::isInstance)
                .map(CreateSubscriptionRequest.class ::cast)
                .map(requestMapper::toSubscriptionRequest)
                .map(subscriptionService::create)
                .map(ignored -> request);
    }
}
