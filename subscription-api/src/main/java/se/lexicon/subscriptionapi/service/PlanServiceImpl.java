package se.lexicon.subscriptionapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.mapper.PlanMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;

@Service @RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final OperatorRepository operatorRepository;
    private final PlanMapper planMapper;
    
    @Override @Transactional
    public PlanResponse create(PlanRequest request) {
        return operatorRepository.findById(request.operator())
            .map(operator -> planMapper.toEntity(request, request.kind().create(), operator))
            .map(planRepository::save)
            .map(planMapper::toResponse)
            .orElse(null);
    }

    @Override @Transactional(readOnly = true)
    public PlanResponse read(Long id) {
        return planRepository.findById(id)
            .map(planMapper::toResponse)
            .orElse(null);
    }
    
    @Override @Transactional
    public PlanResponse update(Long id, PlanRequest request) {
        return planRepository.findById(id)
            .map(existing -> planRepository.save(planMapper.toEntity(request, existing, null)))
            .map(planMapper::toResponse)
            .orElse(null);
    }

    @Override @Transactional
    public void delete(Long id) {
        planRepository.deleteById(id);
    }
}
