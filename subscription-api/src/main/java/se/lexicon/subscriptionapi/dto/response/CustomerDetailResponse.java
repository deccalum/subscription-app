package se.lexicon.subscriptionapi.dto.response;

public record CustomerDetailResponse(
        Long id,
        String address,
        String phoneNumber,
        String preferences
) {
}
