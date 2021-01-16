package pl.jg.eas.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.jg.eas.dao.RoleRepository;
import pl.jg.eas.dao.UserRepository;
import pl.jg.eas.dtos.EditUserForm;
import pl.jg.eas.dtos.NewUserForm;
import pl.jg.eas.entities.Role;
import pl.jg.eas.entities.User;
import pl.jg.eas.exceptions.UserDoesntExistException;
import pl.jg.eas.exceptions.UserWithSuchEmailExistsException;

import javax.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(NewUserForm newUserForm) {

        final String email = newUserForm.getEmail();
        final boolean existsByEmail = userRepository.existsByEmail(email);

        if (existsByEmail) {
            throw new UserWithSuchEmailExistsException(email);
        }
        final String roleName = "ROLE_COMMON_USER";
        final Role role = roleRepository.findRoleByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        final User user = new User();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(newUserForm.getPassword()));
        user.setNickname(newUserForm.getNickname());
        user.addRole(role);

        userRepository.save(user);
    }

    public void editUser(EditUserForm editUserForm, String currentlyLoggedUser) {
        final User user = userRepository.findUserByEmail(currentlyLoggedUser)
                .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        user.setNickname(editUserForm.getNickname());
        user.setPassword(passwordEncoder.encode(editUserForm.getPassword()));

        userRepository.save(user);
    }
}
