package pl.jg.eas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
