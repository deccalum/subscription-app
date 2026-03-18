package se.lexicon.subscriptionapi.service;

import java.util.List;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    CustomerResponse read(Long id);
    CustomerResponse update(Long id, CustomerRequest request);
    void delete(Long id);
    
    List<CustomerResponse> getAll();
    CustomerResponse getEmail(String email);
    CustomerResponse getName(String name);
}
