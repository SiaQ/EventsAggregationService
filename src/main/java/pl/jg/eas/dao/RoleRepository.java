package pl.jg.eas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.entities.Role;
import pl.jg.eas.entities.User;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(long id);
    Optional<Role> findRoleByRoleName(String roleName);
}
