package se.lexicon.subscriptionapi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserFirstNameIgnoreCaseOrUserLastNameIgnoreCase(String firstName, String lastName);
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    List<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    long count();
}
