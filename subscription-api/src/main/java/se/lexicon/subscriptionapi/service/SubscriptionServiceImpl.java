package se.lexicon.subscriptionapi.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.mapper.SubscriptionMapper;
import se.lexicon.subscriptionapi.repository.UserRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public SubscriptionResponse create(SubscriptionRequest request) {
        return planRepository.findById(request.planId())
                .filter(plan -> plan.getOperator().getId().equals(request.operatorId()))
                .flatMap(plan -> userRepository.findById(request.userId()).map(user -> subscriptionMapper.toEntity(request, plan, user)))
                .map(subscriptionRepository::save)
                .map(subscriptionMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public SubscriptionResponse read(Long id) {
        return subscriptionRepository.findById(id).map(subscriptionMapper::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public SubscriptionResponse update(Long id, SubscriptionRequest request) {
        return subscriptionRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(request.status());
                    return existing;
                })
                .map(subscriptionRepository::save)
                .map(subscriptionMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        subscriptionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<SubscriptionResponse> getAll() {
        return subscriptionRepository.findAll().stream().map(subscriptionMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public List<SubscriptionResponse> getUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream().map(subscriptionMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public List<SubscriptionResponse> getStatus(SubscriptionRequest request) {
        return subscriptionRepository.findByStatus(request.status()).stream().map(subscriptionMapper::toResponse).toList();
    }
}
