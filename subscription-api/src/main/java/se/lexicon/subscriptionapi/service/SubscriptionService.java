package se.lexicon.subscriptionapi.service;

import java.util.List;

import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse create(SubscriptionRequest request);
    SubscriptionResponse read(Long id);
    SubscriptionResponse update(Long id, SubscriptionRequest request);
    void delete(Long id);

    List<SubscriptionResponse> getAll();
    List<SubscriptionResponse> getCustomerId(Long customerId);
    List<SubscriptionResponse> getStatus(SubscriptionRequest request);
}
