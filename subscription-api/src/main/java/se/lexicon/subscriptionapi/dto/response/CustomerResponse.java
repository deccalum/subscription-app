package se.lexicon.subscriptionapi.dto.response;

public record CustomerResponse(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
