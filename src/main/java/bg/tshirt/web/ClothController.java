package bg.tshirt.web;

import bg.tshirt.database.dto.*;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothService;
import bg.tshirt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> addCloth(@ModelAttribute @Valid ClothDTO clothDTO, HttpServletRequest request) {
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
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Id must be a positive number"
            ));
        }
        ClothDetailsPageDTO dto = this.clothService.findById(id);

        if (dto == null) {
            throw new NotFoundException("Cloth not found in the system.");
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                dto.getType(), dto
        ));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editClothById(@PathVariable("id") Long id, @ModelAttribute @Valid ClothEditDTO clothDto, HttpServletRequest request) {
        //TODO: Think about changing the model of a cloth for the public id
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Id must be a positive number"
            ));
        }
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

    @GetMapping("/search")
    public ResponseEntity<?> searchCloth(@RequestParam(name = "name") String name,
                                         @RequestParam(name = "sort", defaultValue = "saleCount") String sort,
                                         @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                         @RequestParam(defaultValue = "1") @Min(1) int page) {
        ResponseEntity<?> validationResponse = validateInputs(name, sort);

        if (validationResponse != null) {
            return validationResponse;
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<?> clothPage = this.clothService.findByQuery(pageable, name);

        return buildPagedResponse(clothPage);
    }

    @GetMapping("/category")
    public ResponseEntity<?> searchByCategory(@RequestParam(name = "name") String name,
                                              @RequestParam(name = "sort", defaultValue = "saleCount") String sort,
                                              @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                              @RequestParam(defaultValue = "1") @Min(1) int page) {
        ResponseEntity<?> validationResponse = validateInputs(name, sort);

        if (validationResponse != null) {
            return validationResponse;
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<?> clothPage = this.clothService.findByCategory(pageable, name);

        return buildPagedResponse(clothPage);
    }

    @GetMapping("/type")
    public ResponseEntity<?> searchByType(@RequestParam(name = "name") String name,
                                          @RequestParam(name = "sort", defaultValue = "saleCount") String sort,
                                          @RequestParam(name = "category", required = false) String category,
                                          @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                          @RequestParam(defaultValue = "1") @Min(1) int page) {
        ResponseEntity<?> validationResponse = validateInputs(name, sort);

        if (validationResponse != null) {
            return validationResponse;
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<ClothPageDTO> clothPage = getClothsByTypeAndCategory(pageable, name, category);

        return buildPagedResponse(clothPage);
    }

    private ResponseEntity<?> validateInputs(String name, String sort) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Query parameter 'name' cannot be empty"
            ));
        }

        if (!"saleCount".equals(sort) &&
                !"priceDesc".equals(sort) &&
                !"priceAsc".equals(sort)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Sort must be 'saleCount', 'priceDesc', or 'priceAsc'"
            ));
        }
        return null;
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction = sort.equals("priceDesc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = sort.equals("saleCount") ? "saleCount" : "price";
        return PageRequest.of(page - 1, size, Sort.by(direction, sortProperty));
    }

    private ResponseEntity<?> buildPagedResponse(Page<?> clothPage) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "items_on_page", clothPage.getNumberOfElements(),
                "total_items", clothPage.getTotalElements(),
                "total_pages", clothPage.getTotalPages(),
                "current_page", clothPage.getNumber() + 1,
                "cloths", clothPage.getContent()
        ));
    }

    private Page<ClothPageDTO> getClothsByTypeAndCategory(Pageable pageable, String type, String category) {
        if (category != null && !category.isBlank()) {
            return this.clothService.findByTypeAndCategory(pageable, type, category);
        } else {
            return this.clothService.findByType(pageable, type);
        }
    }
}
