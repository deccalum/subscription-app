package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;

import java.util.List;

public interface OperatorService {
    OperatorResponse create(OperatorRequest request);
    OperatorResponse findById(Long id);
    OperatorResponse findByName(String name);
    List<OperatorResponse> findAll();
}
