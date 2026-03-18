package se.lexicon.subscriptionapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;
import se.lexicon.subscriptionapi.mapper.CustomerMapper;
import se.lexicon.subscriptionapi.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        return Optional.of(request)
                .map(customerMapper::toEntity)
                .map(customer -> {
                    customer.setPassword(passwordEncoder.encode(customer.getPassword()));
                    return customer;
                })
                .map(customerRepository::save)
                .map(customerMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse read(Long id) {
        return customerRepository.findById(id).map(customerMapper::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        return customerRepository.findById(id)
                .map(existing -> {
                    se.lexicon.subscriptionapi.domain.entity.Customer updated = customerMapper.toEntity(request);
                    updated.setId(existing.getId());
                    updated.setPassword(existing.getPassword());
                    return updated;
                })
                .map(customerRepository::save)
                .map(customerMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream().map(customerMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getEmail(String email) {
        return customerRepository.findByEmail(email).map(customerMapper::toResponse).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getName(String name) {
        return customerRepository.findByFirstNameIgnoreCase(name).map(customerMapper::toResponse).orElse(null);
    }
}
