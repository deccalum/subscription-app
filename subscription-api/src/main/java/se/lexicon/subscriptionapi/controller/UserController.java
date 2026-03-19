package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.domain.constant.UserCredentials;
import se.lexicon.subscriptionapi.dto.request.UserRequest;
import se.lexicon.subscriptionapi.dto.response.UserResponse;
import se.lexicon.subscriptionapi.service.UserService;

@Tag(
    name = "Users", 
    description = "User endpoints (public register; other endpoints require JWT)."
)
@RestController
@RequestMapping("/api/v1/Users")
@RequiredArgsConstructor
public class UserController {
    private final UserService user;

    @PostMapping
    @SecurityRequirements
    @Operation(
        summary = "{api.user.create.summary}", 
        description = "{api.user.create.description}"
    )
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(user.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.user.read.summary}", 
        description = "{api.user.read.description}", 
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserResponse> read(@PathVariable Long id) {

        return ResponseEntity
        .ok(user.read(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "{api.user.update.summary}", 
        description = "{api.user.update.description}", 
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {

        return ResponseEntity
        .ok(user.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "{api.user.delete.summary}", 
        description = "{api.user.delete.description}", 
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        user.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.user.getAll.summary}", 
        description = "{api.user.getAll.description}", 
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<UserResponse>> getAll() {

        return ResponseEntity
        .ok(user.getAll());
    }

    @GetMapping("/credentials")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.user.getCredentials.summary}", 
        description = "{api.user.getCredentials.description}", 
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserResponse> getCredentials(@RequestParam UserCredentials credential) {

        return ResponseEntity
        .ok(user.getCredentials(credential));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    @Operation(
        summary = "{api.user.getEmail.summary}", 
        description = "{api.user.getEmail.description}", 
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserResponse> getEmail(@PathVariable String email) {
        
        return ResponseEntity
        .ok(user.getEmail(email));
    }
}
