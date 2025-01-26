package bg.tshirt.web;

import bg.tshirt.database.dto.*;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothService;
import bg.tshirt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                "status", "success",
                "message", "Cloth added successfully!",
                "cloth_name", clothDTO.getName(),
                "added_by", admin.getEmail()
        ));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getClothById(@PathVariable("id") Long id) {
        ClothPageDTO dto = this.clothService.findById(id);

        if (dto == null) {
            throw new NotFoundException("Cloth not found in the system.");
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                dto.getType(), dto
        ));
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<?> editClothById(@PathVariable("id") Long id, @ModelAttribute @Valid ClothEditDTO clothDto, HttpServletRequest request) {
        //TODO: Think about changing the model of a cloth for the public id
        UserDTO admin = this.userService.validateAdmin(request);

        if (!this.clothService.editCloth(clothDto, id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", String.format("Cloth with id: %d has no images", id)));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Cloth edited successfully!",
                "cloth_name", clothDto.getName(),
                "edited_by", admin.getEmail()
        ));
    }
}
