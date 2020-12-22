package pl.jg.eas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.entities.User;

import javax.validation.constraints.Email;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
