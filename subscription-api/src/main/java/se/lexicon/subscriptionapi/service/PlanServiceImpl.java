package se.lexicon.subscriptionapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.mapper.PlanMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final OperatorRepository operatorRepository;
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public PlanResponse create(PlanRequest request) {
        return operatorRepository.findById(request.operator())
                .map(operator -> planMapper.toEntity(request, request.kind().create(), operator))
                .map(planRepository::save)
                .map(planMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse read(Long id) {
        return planRepository.findById(id).map(planMapper::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public PlanResponse update(Long id, PlanRequest request) {
        return planRepository.findById(id).map(existing -> planRepository.save(planMapper.toEntity(request, existing, null))).map(planMapper::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        planRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getByName(String name) {
        return planRepository.findByNameIgnoreCase(name).map(planMapper::toResponse).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findByOperatorName(String operatorName) {
        return planRepository.findAllByOperatorNameIgnoreCase(operatorName).stream().map(planMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findByOperatorId(Long operatorId) {
        return planRepository.findByOperatorId(operatorId).stream().map(planMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return planRepository.findByPriceBetween(minPrice, maxPrice).stream().map(planMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findByStatus(PlanStatus status) {
        return planRepository.findByStatus(status).stream().map(planMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOperatorId(Long operatorId) {
        return planRepository.existsByOperatorId(operatorId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByOperatorIdAndStatus(Long operatorId, PlanStatus status) {
        return planRepository.countByOperatorIdAndStatus(operatorId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findAllInternetPlans() {
        return planRepository.findAllInternetPlans().stream().map(planMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findAllCellularPlans() {
        return planRepository.findAllCellularPlans().stream().map(planMapper::toResponse).collect(Collectors.toList());
    }
}
