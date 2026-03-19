package se.lexicon.subscriptionapi.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.mapper.OperatorMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;


@Service @RequiredArgsConstructor
public class OperatorServiceImpl implements OperatorService {
    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;

    @Override @Transactional
    public OperatorResponse create(OperatorRequest request) {
        return Optional.of(request).map(operatorMapper::toEntity).map(operatorRepository::save).map(operatorMapper::toResponse).orElse(null);
    }

    @Override @Transactional(readOnly = true)
    public OperatorResponse read(Long id) {
        return operatorRepository.findById(id).map(operatorMapper::toResponse).orElse(null);
    }

    @Override @Transactional
    public OperatorResponse update(Long id, OperatorRequest request) {
        return operatorRepository.findById(id).map(existing -> operatorRepository.save(operatorMapper.toEntity(request))).map(operatorMapper::toResponse).orElse(null);
    }

    @Override @Transactional
    public void delete(Long id) {
        operatorRepository.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public OperatorResponse getName(String name) {
        return operatorRepository.findByName(name).map(operatorMapper::toResponse).orElse(null);
    }

    @Override @Transactional(readOnly = true)
    public List<OperatorResponse> getAll() {
        return operatorRepository.findAll().stream().map(operatorMapper::toResponse).toList();
    }
}
