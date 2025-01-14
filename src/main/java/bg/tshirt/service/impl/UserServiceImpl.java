package bg.tshirt.service.impl;

import bg.tshirt.database.dto.UserRegistrationDTO;
import bg.tshirt.database.entity.User;
import bg.tshirt.database.entity.enums.Role;
import bg.tshirt.database.repository.UserRepository;
import bg.tshirt.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final static int ADMINS_COUNT = 2;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerUser(UserRegistrationDTO registrationDTO) {
        if (this.userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        Set<Role> roles = determineRoles();

        User user = new User(
                registrationDTO.getEmail(),
                this.passwordEncoder.encode(registrationDTO.getPassword()),
                registrationDTO.getAddress(),
                roles
        );

        userRepository.save(user);
    }


    private Set<Role> determineRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        if (userRepository.count() < ADMINS_COUNT) {
            roles.add(Role.ADMIN);
        }
        return roles;
    }
}
