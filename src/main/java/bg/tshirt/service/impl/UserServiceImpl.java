package bg.tshirt.service.impl;

import bg.tshirt.config.JwtTokenProvider;
import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.dto.UserProfileDTO;
import bg.tshirt.database.dto.UserRegistrationDTO;
import bg.tshirt.database.entity.User;
import bg.tshirt.database.entity.enums.Role;
import bg.tshirt.database.repository.UserRepository;
import bg.tshirt.exceptions.EmailAlreadyInUseException;
import bg.tshirt.exceptions.ForbiddenException;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.exceptions.UnauthorizedException;
import bg.tshirt.service.OrderService;
import bg.tshirt.service.UserService;
import bg.tshirt.utils.PhoneNumberUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final PhoneNumberUtils phoneNumberUtils;
    private final static int ADMINS_COUNT = 4;
    private static final int MODERATOR_COUNT = 4;

    public UserServiceImpl(UserRepository userRepository,
                           JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper,
                           PhoneNumberUtils phoneNumberUtils) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.phoneNumberUtils = phoneNumberUtils;
    }

    @Override
    @Transactional
    public void registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        this.phoneNumberUtils.validateBulgarianPhoneNumber(registrationDTO.getPhoneNumber());

        Set<Role> roles = determineRoles();

        User user = new User(
                registrationDTO.getEmail(),
                this.passwordEncoder.encode(registrationDTO.getPassword()),
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                this.phoneNumberUtils.formatPhoneNumber(registrationDTO.getPhoneNumber()),
                registrationDTO.getCity(),
                registrationDTO.getRegion(),
                registrationDTO.getAddress(),
                roles
        );

        saveUser(user);
    }

    @Override
    public UserDTO findByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .map(user -> new UserDTO(user.getEmail(), user.getAddress(), user.getRoles()))
                .orElse(null);
    }

    @Override
    public UserDTO validateAdmin(HttpServletRequest request) {
        String token = extractToken(request);
        validateAdminRole(token);
        return findUser(token);
    }

    @Override
    public UserDTO validateUser(HttpServletRequest request) {
        String token = extractToken(request);
        return findUser(token);
    }

    @Override
    @Transactional
    public UserProfileDTO getUserProfile(HttpServletRequest request) {
        UserDTO userDTO = validateUser(request);

        return userRepository.findByEmail(userDTO.getEmail())
                .map(this::mapToUserProfileDTO)
                .orElse(null);
    }

    @Override
    public List<String> getUserRoles(String accessToken) {
        return this.jwtTokenProvider.getRoles(accessToken);
    }

    @Override
    public boolean resetUserPassword(UserDTO userDTO) {
        Optional<User> optional = this.userRepository.findByEmail(userDTO.getEmail());

        if (optional.isEmpty()) {
            return false;
        }
        optional.get().setPassword(this.passwordEncoder.encode(userDTO.getPassword()));
        this.userRepository.save(optional.get());

        return true;
    }

    private void saveUser(User user) {
        this.userRepository.save(user);
    }

    private UserProfileDTO mapToUserProfileDTO(User user) {
        return modelMapper.map(user, UserProfileDTO.class);
    }

    private void validateAdminRole(String token) {
        List<?> rolesFromJwt = this.jwtTokenProvider.getRolesFromJwt(token);
        if (rolesFromJwt == null || rolesFromJwt.isEmpty()) {
            throw new ForbiddenException("Access denied. No roles found in the token.");
        }
        if (!rolesFromJwt.contains("ADMIN")) {
            throw new ForbiddenException("Access denied. Admin privileges required.");
        }
    }

    private UserDTO findUser(String token) {
        String email = jwtTokenProvider.getEmailFromJwt(token);
        if (email == null || email.isEmpty()) {
            throw new UnauthorizedException("Email not found in token.");
        }

        UserDTO user = findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found in the system.");
        }
        return user;
    }

    private String extractToken(HttpServletRequest request) {
        String token = this.jwtTokenProvider.getJwtFromRequest(request);
        if (!this.jwtTokenProvider.isValidToken(token, request)) {
            throw new UnauthorizedException("Invalid or expired token.");
        }
        return token;
    }

    private Set<Role> determineRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        if (userRepository.count() < MODERATOR_COUNT) {
            roles.add(Role.MODERATOR);
        }

        if (userRepository.count() < ADMINS_COUNT) {
            roles.add(Role.ADMIN);
        }

        return roles;
    }
}
