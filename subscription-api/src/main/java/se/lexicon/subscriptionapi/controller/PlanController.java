package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.service.PlanService;

@Tag(name = "Plans", description = "Plan management endpoints.")
@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService plan;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "{api.plan.create.summary}", description = "{api.plan.create.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody PlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plan.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(summary = "{api.plan.read.summary}", description = "{api.plan.read.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PlanResponse> read(@PathVariable Long id) {
        return ResponseEntity.ok(plan.read(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "{api.plan.update.summary}", description = "{api.plan.update.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PlanResponse> update(@PathVariable Long id, @Valid @RequestBody PlanRequest request) {
        return ResponseEntity.ok(plan.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "{api.plan.delete.summary}", description = "{api.plan.delete.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plan.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<PlanResponse> getByName(@PathVariable String name) {
        return ResponseEntity.ok(plan.getByName(name));
    }

    @GetMapping("/operator/{operatorId}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<java.util.List<PlanResponse>> getByOperatorId(@PathVariable Long operatorId) {
        return ResponseEntity.ok(plan.findByOperatorId(operatorId));
    }

    @GetMapping("/operator/name/{operatorName}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<java.util.List<PlanResponse>> getByOperatorName(@PathVariable String operatorName) {
        return ResponseEntity.ok(plan.findByOperatorName(operatorName));
    }

    @GetMapping("/price")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<java.util.List<PlanResponse>> getByPriceBetween(@RequestParam java.math.BigDecimal min, @RequestParam java.math.BigDecimal max) {
        return ResponseEntity.ok(plan.findByPriceBetween(min, max));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<java.util.List<PlanResponse>> getByStatus(@RequestParam se.lexicon.subscriptionapi.domain.constant.PlanStatus status) {
        return ResponseEntity.ok(plan.findByStatus(status));
    }

    @GetMapping("/exists/operator/{operatorId}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<Boolean> existsByOperator(@PathVariable Long operatorId) {
        return ResponseEntity.ok(plan.existsByOperatorId(operatorId));
    }

    @GetMapping("/count/operator/{operatorId}/status")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<Long> countByOperatorAndStatus(@PathVariable Long operatorId, @RequestParam se.lexicon.subscriptionapi.domain.constant.PlanStatus status) {
        return ResponseEntity.ok(plan.countByOperatorIdAndStatus(operatorId, status));
    }

    @GetMapping("/internet")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<java.util.List<PlanResponse>> getInternetPlans() {
        return ResponseEntity.ok(plan.findAllInternetPlans());
    }

    @GetMapping("/cellular")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<java.util.List<PlanResponse>> getCellularPlans() {
        return ResponseEntity.ok(plan.findAllCellularPlans());
    }
}
