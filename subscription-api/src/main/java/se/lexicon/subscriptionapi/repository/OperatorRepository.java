package se.lexicon.subscriptionapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.subscriptionapi.domain.entity.Operator;


@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Optional<Operator> findByName(String name);

    boolean existsByName(String name);
}
