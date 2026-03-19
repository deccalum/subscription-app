package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.dto.request.LoginRequest;
import se.lexicon.subscriptionapi.dto.request.UserRequest;
import se.lexicon.subscriptionapi.security.JwtTokenProvider;
import se.lexicon.subscriptionapi.security.TokenBlacklistService;
import se.lexicon.subscriptionapi.service.UserService;

@Tag(
    name = "Auth",
    description = "Authentication endpoints (login/register public, logout requires token)."
)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService user;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
        summary = "{api.auth.login.summary}",
        description = "{api.auth.login.description}"
    )
    public ResponseEntity<Map<String, Object>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        Map<String, Object> body = new HashMap<>();
        body.put("accessToken", jwt);
        body.put("tokenType", "Bearer");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(
        summary = "{api.auth.register.summary}",
        description = "{api.auth.register.description}"
    )
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {

        return ResponseEntity.ok(user.create(request));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "{api.auth.logout.summary}",
        description = "{api.auth.logout.description}",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<String> logout(jakarta.servlet.http.HttpServletRequest request) {

        String jwt = getJwtFromRequest(request);
        if (jwt != null) {
            log.info("Logging out user with token: {}", jwt.substring(0, 10) + "...");
            try {
                Date expirationDate = tokenProvider.getExpirationDateFromJWT(jwt);
                long diff = expirationDate.getTime() - Instant.now().toEpochMilli();
                if (diff > 0) {
                    tokenBlacklistService.blacklistToken(jwt, Duration.ofMillis(diff));
                    log.info("Token blacklisted for {} ms", diff);
                } else {
                    log.warn("Token already expired, no need to blacklist");
                }
            } catch (Exception e) {
                log.error("Error during logout/blacklisting: {}", e.getMessage());
                return ResponseEntity.status(401).body("Invalid token");
            }
            return ResponseEntity.ok("Logged out successfully");
        } else {
            log.warn("Logout called without a valid JWT token in Authorization header");
            return ResponseEntity.status(401).body("Authentication token is required for logout");
        }
    }

    private String getJwtFromRequest(jakarta.servlet.http.HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
