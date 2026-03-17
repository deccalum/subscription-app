package se.lexicon.subscriptionapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.UserRole;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.CustomerDetail;
import se.lexicon.subscriptionapi.dto.request.CustomerDetailRequest;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.CustomerDetailMapper;
import se.lexicon.subscriptionapi.mapper.CustomerMapper;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.service.CustomerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerDetailMapper customerDetailMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CustomerResponse register(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already exists: " + request.email());
        }
        Customer customer = customerMapper.toEntity(request);
        // encode password
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        // default role
        if (customer.getRoles() == null || customer.getRoles().isEmpty()) {
            customer.getRoles().add(UserRole.ROLE_USER);
        }
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponse updateProfile(Long customerId, CustomerDetailRequest detailRequest) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        if (customer.getCustomerDetail() == null) {
            CustomerDetail detail = customerDetailMapper.toEntity(detailRequest);
            customer.setCustomerDetail(detail);
        } else {
            CustomerDetail detail = customer.getCustomerDetail();
            detail.setAddress(detailRequest.address());
            detail.setPhoneNumber(detailRequest.phoneNumber());
            detail.setPreferences(detailRequest.preferences());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }
}
