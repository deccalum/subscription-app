package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;

import java.util.List;

public interface OperatorService {
    OperatorResponse create(OperatorRequest request);
    OperatorResponse read(Long id);
    OperatorResponse update(Long id, OperatorRequest request);
    void delete(Long id);

    OperatorResponse getName(String name);
    OperatorResponse getNameIgnoreCase(String name);
    List<OperatorResponse> getAll();

    boolean existsByName(String name);
    boolean existsByNameIgnoreCase(String name);
}
