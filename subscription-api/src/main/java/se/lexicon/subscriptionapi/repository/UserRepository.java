package se.lexicon.subscriptionapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import se.lexicon.subscriptionapi.domain.constant.UserCredentials;
import se.lexicon.subscriptionapi.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    void deleteById(Long id);
    Optional<User> findByRolesContaining (UserCredentials credentials);
    Optional<User> findByEmail(String email);
    Optional<User> findByFirstNameIgnoreCase(String firstName);
    Optional<User> findByLastNameIgnoreCase(String lastName);
    boolean existsByEmail(String email);
}
