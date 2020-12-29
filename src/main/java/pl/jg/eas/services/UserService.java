package pl.jg.eas.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.jg.eas.dao.RoleRepository;
import pl.jg.eas.dao.UserRepository;
import pl.jg.eas.dtos.EditUserForm;
import pl.jg.eas.dtos.NewUserForm;
import pl.jg.eas.entities.Role;
import pl.jg.eas.entities.User;
import pl.jg.eas.exceptions.RoleDoesntExistException;
import pl.jg.eas.exceptions.UserDoesntExistException;
import pl.jg.eas.exceptions.UserWithSuchEmailExistsException;

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

    public void registerUser(NewUserForm newUserForm) {
        final User user = userRepository.findUserByEmail(newUserForm.getEmail())
                .orElse(new User());
        final String roleName = "ROLE_COMMON_USER";
        final Role role = roleRepository.findRoleByRoleName(roleName)
                .orElseThrow(() -> new RoleDoesntExistException(roleName));

        if (user.getEmail() != null) {
            throw new UserWithSuchEmailExistsException(newUserForm.getEmail());
        } else {
            user.setEmail(newUserForm.getEmail());
            user.setPassword(passwordEncoder.encode(newUserForm.getPassword()));
            user.setNickname(newUserForm.getNickname());
            user.addRole(role);

            userRepository.save(user);
        }
    }

    public void editUser(EditUserForm editUserForm, String currentlyLoggedUser) {
        final User user = userRepository.findUserByEmail(currentlyLoggedUser).orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        user.setNickname(editUserForm.getNickname());
        user.setPassword(passwordEncoder.encode(editUserForm.getPassword()));

        userRepository.save(user);
    }
}
