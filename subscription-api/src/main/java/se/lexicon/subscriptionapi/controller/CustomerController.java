package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.dto.request.CustomerDetailRequest;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;
import se.lexicon.subscriptionapi.service.CustomerService;

import java.util.List;

@Tag(name = "Customers", description = "Customer endpoints (public register; other endpoints require JWT).")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @SecurityRequirements // Public (no auth)
    @Operation(
            summary = "Register a new customer",
            description = "Public endpoint. Creates a customer account.\n\nRoles: Public"
    )
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.register(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get customer by ID",
            description = "Requires JWT.\n\nRoles: USER, ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Get customer by email",
            description = "Requires JWT.\n\nRoles: USER, ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.findByEmail(email));
    }

    @GetMapping
    @Operation(
            summary = "Get all customers",
            description = "Requires JWT.\n\nRoles: USER, ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @PutMapping("/{id}/profile")
    @Operation(
            summary = "Update customer profile",
            description = "Requires JWT.\n\nRoles: USER, ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CustomerResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDetailRequest detailRequest) {
        return ResponseEntity.ok(customerService.updateProfile(id, detailRequest));
    }
}