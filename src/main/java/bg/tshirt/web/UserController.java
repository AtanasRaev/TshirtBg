package bg.tshirt.web;

import bg.tshirt.config.JwtTokenProvider;
import bg.tshirt.database.dto.*;
import bg.tshirt.service.RefreshTokenService;
import bg.tshirt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService,
                          JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager,
                          RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        UserProfileDTO user = this.userService.getUserProfile(request);

        Map<String, Object> profile = Map.of(
                "email", user.getEmail(),
                "address", user.getAddress(),
                "orders", user.getOrders()
        );

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationDTO registrationDTO, HttpServletRequest request) {
        this.userService.registerUser(registrationDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registrationDTO.getEmail(),
                        registrationDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String currentFingerprint = jwtTokenProvider.generateDeviceFingerprint(request);
        String accessToken = jwtTokenProvider.generateAccessToken(authentication, currentFingerprint);
        String refreshToken = jwtTokenProvider.generateRefreshToken(registrationDTO.getEmail(), currentFingerprint);

        this.refreshTokenService.saveNewToken(
                jwtTokenProvider.getJtiFromJwt(refreshToken),
                registrationDTO.getEmail(),
                jwtTokenProvider.getExpirationDate(refreshToken)
        );

        return ResponseEntity.ok(new UserResponseDTO("success", "Registration successful", accessToken, refreshToken, this.userService.getUserRoles(accessToken)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO loginRequest, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String currentFingerprint = jwtTokenProvider.generateDeviceFingerprint(request);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication, currentFingerprint);
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getEmail(), currentFingerprint);

        this.refreshTokenService.saveNewToken(
                jwtTokenProvider.getJtiFromJwt(refreshToken),
                loginRequest.getEmail(),
                jwtTokenProvider.getExpirationDate(refreshToken)
        );

        return ResponseEntity.ok(new UserResponseDTO("success", "Login successful", accessToken, refreshToken, this.userService.getUserRoles(accessToken)));
    }
}