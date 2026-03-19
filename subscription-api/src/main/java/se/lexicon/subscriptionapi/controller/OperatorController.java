package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.dto.request.*;
import se.lexicon.subscriptionapi.dto.response.ChangeRequestResponse;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.service.ChangeRequestService;
import se.lexicon.subscriptionapi.service.OperatorService;

@Tag(
    name = "Operators",
    description = "Operator management endpoints."
)
@RestController
@RequestMapping("/api/v1/operators")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OperatorController {

    private final OperatorService operatorService;
    private final ChangeRequestService changeRequestService;

    @PostMapping("/requests/plans")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(
        summary = "{api.operator.submitCreatePlan.summary}",
        description = "{api.operator.submitCreatePlan.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChangeRequestResponse> submitCreatePlan(
            @Valid @RequestBody CreatePlanChangeRequest dto, Authentication auth) {

        return ResponseEntity
        .status(HttpStatus.CREATED)
                .body(changeRequestService.submitCreatePlan(dto, auth.getName()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "{api.operator.create.summary}",
        description = "{api.operator.create.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OperatorResponse> create(@Valid @RequestBody OperatorRequest request) {

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(operatorService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.operator.read.summary}",
        description = "{api.operator.read.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OperatorResponse> read(@PathVariable Long id) {

        return ResponseEntity
        .ok(operatorService.read(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.operator.getName.summary}",
        description = "{api.operator.getName.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OperatorResponse> getName(@RequestParam String name) {

        return ResponseEntity
        .ok(operatorService.getName(name));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.operator.getAll.summary}",
        description = "{api.operator.getAll.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<OperatorResponse>> get() {

        return ResponseEntity
        .ok(operatorService.getAll());
    }

    @PutMapping("/requests/plans/{planId}")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(
        summary = "{api.operator.submitUpdatePlan.summary}",
        description = "{api.operator.submitUpdatePlan.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChangeRequestResponse> submitUpdatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody UpdatePlanChangeRequest dto,
            Authentication auth) {

        UpdatePlanChangeRequest payload = new UpdatePlanChangeRequest(
            planId,
            dto.kind(),
            dto.name(),
            dto.price(),
            dto.status(),
            dto.uploadSpeedMbps(),
            dto.downloadSpeedMbps(),
            dto.networkGeneration(),
            dto.dataLimitGb(),
            dto.callCostPerMinute(),
            dto.smsCostPerMessage());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(changeRequestService.submitUpdatePlan(payload, auth.getName()));
    }

    @DeleteMapping("/requests/plans/{planId}")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(
        summary = "{api.operator.submitDeletePlan.summary}",
        description = "{api.operator.submitDeletePlan.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChangeRequestResponse> submitDeletePlan(
            @PathVariable Long planId, Authentication auth) {

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(changeRequestService.submitDeletePlan(new DeletePlanChangeRequest(planId), auth.getName()));
    }

    @PutMapping("/requests/operator/{operatorId}")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(
        summary = "{api.operator.submitUpdateOperator.summary}",
        description = "{api.operator.submitUpdateOperator.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChangeRequestResponse> submitUpdateOperator(
            @PathVariable Long operatorId,
            @Valid @RequestBody UpdateOperatorChangeRequest dto,
            Authentication auth) {

        UpdateOperatorChangeRequest payload =
            new UpdateOperatorChangeRequest(operatorId, dto.newName());
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(changeRequestService.submitUpdateOperator(payload, auth.getName()));
    }

    @PostMapping("/requests/subscriptions")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(
        summary = "{api.operator.submitCreateSubscription.summary}",
        description = "{api.operator.submitCreateSubscription.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChangeRequestResponse> submitCreateSubscription(
            @Valid @RequestBody CreateSubscriptionChangeRequest dto, Authentication auth) {

        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(changeRequestService.submitCreateSubscription(dto, auth.getName()));
    }

    @GetMapping("/requests/mine")
    @PreAuthorize("hasRole('OPERATOR')")
    @Operation(
        summary = "{api.operator.getMyRequests.summary}",
        description = "{api.operator.getMyRequests.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<ChangeRequestResponse>> getMyRequests(Authentication auth) {

        return ResponseEntity
        .ok(changeRequestService.getMyRequests(auth.getName()));
    }
}