package pl.jg.eas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByRoleName(String roleName);
}
