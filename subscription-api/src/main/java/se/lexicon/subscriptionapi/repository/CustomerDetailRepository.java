package se.lexicon.subscriptionapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.entity.CustomerDetail;

@Repository
public interface CustomerDetailRepository extends JpaRepository<CustomerDetail, Long> {
}
