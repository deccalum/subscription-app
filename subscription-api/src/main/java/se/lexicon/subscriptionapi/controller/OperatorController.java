package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.service.OperatorService;

import java.util.List;

@Tag(name = "Operators", description = "Operator management endpoints.")
@RestController
@RequestMapping("/api/v1/operators")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OperatorController {

    private final OperatorService operatorService;

    @PostMapping
    @Operation(summary = "Create operator", description = "Requires JWT.\n\nRoles: ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorResponse> create(@Valid @RequestBody OperatorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operatorService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get operator by ID", description = "Requires JWT.\n\nRoles: USER, ADMIN")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OperatorResponse> read(@PathVariable Long id) {
        return ResponseEntity.ok(operatorService.read(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search operator by name", description = "Requires JWT.\n\nRoles: USER, ADMIN")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OperatorResponse> findByName(@RequestParam String name) {
        return ResponseEntity.ok(operatorService.getName(name));
    }

    @GetMapping
    @Operation(summary = "Get all operators", description = "Requires JWT.\n\nRoles: USER, ADMIN")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OperatorResponse>> get() {
        return ResponseEntity.ok(operatorService.getAll());
    }
}