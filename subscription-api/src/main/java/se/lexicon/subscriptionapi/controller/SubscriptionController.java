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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.service.SubscriptionService;

@Tag(
    name = "Subscriptions",
    description = "Subscription management endpoints."
)
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscription;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.create.summary}",
        description = "{api.subscription.create.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionRequest request) {

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(subscription.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.read.summary}",
        description = "{api.subscription.read.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<SubscriptionResponse> read(@PathVariable Long id) {

        return ResponseEntity
            .ok(subscription.read(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.update.summary}",
        description = "{api.subscription.update.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<SubscriptionResponse> update(@PathVariable Long id, @Valid @RequestBody SubscriptionRequest request) {

        return ResponseEntity
            .ok(subscription.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.delete.summary}",
        description = "{api.subscription.delete.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        subscription.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.getAll.summary}",
        description = "{api.subscription.getAll.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<SubscriptionResponse>> getAll() {

        return ResponseEntity
            .ok(subscription.getAll());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.getUserId.summary}",
        description = "{api.subscription.getUserId.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<SubscriptionResponse>> getUserId(@PathVariable Long userId) {

        return ResponseEntity
            .ok(subscription.getUserId(userId));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.subscription.getStatus.summary}",
        description = "{api.subscription.getStatus.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<SubscriptionResponse>> getStatus(@RequestParam SubscriptionStatus status) {

        return ResponseEntity
            .ok(subscription.getStatus(status));
    }
}
