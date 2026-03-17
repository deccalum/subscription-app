package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{blank}") 
        @Email(message = "{invalidEmail}") 
        String email,

        @NotBlank(message = "{blank}") 
        String password
) {}
