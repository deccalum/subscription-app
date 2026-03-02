package se.lexicon.subscriptionapi.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import se.lexicon.subscriptionapi.domain.entity.Customer;

import java.util.Optional;

@Repository
@Validated // <--- Required to trigger validation on methods if you want to enable validation in repository methods
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(@NotBlank(message = "email cannot be blank") @Email(message = "invalid email format") String email);

    boolean existsByEmail(@NotBlank(message = "email cannot be blank") @Email(message = "invalid email format") String email);
}
