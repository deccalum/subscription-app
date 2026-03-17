package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "{blank}") 
        @Email(message = "{invalidEmail}")
        @Size(min = 5, max = 100, message = "{invalidLength}") 
        String email,

        @NotBlank(message = "{blank}") 
        @Size(max = 50, message = "{invalidLength}") 
        String firstName,

        @NotBlank(message = "{blank}") 
        @Size(max = 50, message = "{invalidLength}") 
        String lastName,

        @NotBlank(message = "{blank}") 
        @Size(min = 8, message = "{invalidLength}") 
        String password
) {}
