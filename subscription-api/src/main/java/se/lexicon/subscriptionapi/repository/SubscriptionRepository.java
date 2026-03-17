package se.lexicon.subscriptionapi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByCustomerFirstNameIgnoreCaseOrCustomerLastNameIgnoreCase(String firstName, String lastName);
    List<Subscription> findByCustomerId(Long customerId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    List<Subscription> findByCustomerIdAndStatus(Long customerId, SubscriptionStatus status);
    long count();
}
