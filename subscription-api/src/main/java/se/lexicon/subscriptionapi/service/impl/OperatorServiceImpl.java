package se.lexicon.subscriptionapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.OperatorMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.service.OperatorService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorServiceImpl implements OperatorService {

    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;

    @Override
    @Transactional
    public OperatorResponse create(OperatorRequest request) {
        if (operatorRepository.existsByName(request.name())) {
            throw new BusinessRuleException("Operator name already exists: " + request.name());
        }
        Operator operator = operatorMapper.toEntity(request);
        Operator savedOperator = operatorRepository.save(operator);
        return operatorMapper.toResponse(savedOperator);
    }

    @Override
    @Transactional(readOnly = true)
    public OperatorResponse findById(Long id) {
        return operatorRepository.findById(id)
                .map(operatorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public OperatorResponse findByName(String name) {
        return operatorRepository.findByName(name)
                .map(operatorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperatorResponse> findAll() {
        return operatorRepository.findAll().stream()
                .map(operatorMapper::toResponse)
                .collect(Collectors.toList());
    }
}
