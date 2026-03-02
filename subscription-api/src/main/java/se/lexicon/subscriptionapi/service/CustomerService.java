package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.CustomerDetailRequest;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse register(CustomerRequest request);
    CustomerResponse findById(Long id);
    CustomerResponse findByEmail(String email);
    List<CustomerResponse> findAll();
    CustomerResponse updateProfile(Long customerId, CustomerDetailRequest detailRequest);
}
