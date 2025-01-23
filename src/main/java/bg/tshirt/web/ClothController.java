package bg.tshirt.web;

import bg.tshirt.config.JwtTokenProvider;
import bg.tshirt.database.dto.ClothAddDTO;
import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.service.ClothService;
import bg.tshirt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloth")
public class ClothController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClothService clothService;

    public ClothController(UserService userService,
                           JwtTokenProvider jwtTokenProvider,
                           ClothService clothService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.clothService = clothService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCloth(@ModelAttribute @Valid ClothAddDTO clothDTO, HttpServletRequest request) {
        String token = this.jwtTokenProvider.getJwtFromRequest(request);

        if (!this.jwtTokenProvider.isValidToken(token, request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token."));
        }

        List<?> rolesFromJwt = this.jwtTokenProvider.getRolesFromJwt(token);

        if (rolesFromJwt == null || rolesFromJwt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. No roles found in the token."));
        }

        if (!rolesFromJwt.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required."));
        }

        String email = this.jwtTokenProvider.getEmailFromJwt(token);
        UserDTO admin = this.userService.findByEmail(email);

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found in the system."));
        }

        if (!this.clothService.addCloth(clothDTO)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Cloth with this model and type already exists."));
        }

        return ResponseEntity.ok(Map.of("message", "Cloth added successfully!", "clothName", clothDTO.getName(), "addedBy", admin.getEmail()));
    }
}
