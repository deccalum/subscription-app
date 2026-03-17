package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerDetailRequest(
        @NotBlank(message = "{blank}") 
        String address,

        @NotBlank(message = "{blank}") 
        @Pattern(regexp = "^\\+?[0-9\\s-]{7,20}$", message = "{invalidNumber}")
        String phoneNumber,
        
        String preferences
) {}