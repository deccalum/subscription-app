package se.lexicon.subscriptionapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findById(Long id);
    void deleteById(Long id);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByFirstNameIgnoreCase(String firstName);
    Optional<Customer> findByLastNameIgnoreCase(String lastName);
    boolean existsByEmail(String email);
}
