package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.domain.constant.RequestStatus;
import se.lexicon.subscriptionapi.dto.request.RejectChangeRequest;
import se.lexicon.subscriptionapi.dto.response.ChangeRequestResponse;
import se.lexicon.subscriptionapi.service.AdministrationService;

@Tag(name = "Admin", description = "Change request review endpoints.")
@RestController
@RequestMapping("/api/v1/admin/requests")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final AdministrationService administrationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "{api.admin.getByStatus.summary}", description = "{api.admin.getByStatus.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ChangeRequestResponse>> getByStatus(@RequestParam(defaultValue = "PENDING") RequestStatus status) {
        return ResponseEntity.ok(administrationService.getByStatus(status));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "{api.admin.approve.summary}", description = "{api.admin.approve.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ChangeRequestResponse> approveRequest(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(administrationService.approveRequest(id, authentication.getName()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "{api.admin.reject.summary}", description = "{api.admin.reject.description}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ChangeRequestResponse> rejectRequest(@PathVariable Long id, @Valid @RequestBody RejectChangeRequest body, Authentication authentication) {
        return ResponseEntity.ok(administrationService.rejectRequest(id, body.reason(), authentication.getName()));
    }
}
