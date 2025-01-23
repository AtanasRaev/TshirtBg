package bg.tshirt.web;

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

import java.util.Map;

@RestController
@RequestMapping("/cloth")
public class ClothController {
    private final UserService userService;
    private final ClothService clothService;

    public ClothController(UserService userService, ClothService clothService) {
        this.userService = userService;
        this.clothService = clothService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCloth(@ModelAttribute @Valid ClothAddDTO clothDTO, HttpServletRequest request) {
        UserDTO admin = this.userService.validateAdmin(request);

        if (!this.clothService.addCloth(clothDTO)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Cloth with this model and type already exists."));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Cloth added successfully!",
                "clothName", clothDTO.getName(),
                "addedBy", admin.getEmail()
        ));
    }
}
