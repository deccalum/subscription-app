package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OperatorRequest(
        Long operator,

        @NotBlank(message = "{blank}") 
        @Size(max = 100, message = "{invalidLength}") 
        String name
) {}
