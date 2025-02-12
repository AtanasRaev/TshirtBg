package bg.tshirt.web;

import bg.tshirt.database.dto.*;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothingService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clothes")
public class ClothingController {
    private final UserService userService;
    private final ClothingService clothService;

    public ClothingController(UserService userService, ClothingService clothService) {
        this.userService = userService;
        this.clothService = clothService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCloth(@ModelAttribute @Valid ClothingDTO clothDTO,
                                      HttpServletRequest request) {
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getClothById(@PathVariable("id") Long id) {
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Id must be a positive number"
            ));
        }
        ClothingDetailsPageDTO dto = this.clothService.findById(id);

        if (dto == null) {
            throw new NotFoundException("Cloth not found in the system.");
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "clothing", dto
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editClothById(@PathVariable("id") Long id,
                                           @ModelAttribute @Valid ClothEditDTO clothDto,
                                           HttpServletRequest request) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClothById(@PathVariable("id") Long id,
                                             HttpServletRequest request) {
        if (id == null || id < 1) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Id must be a positive number"
            ));
        }

        UserDTO admin = this.userService.validateAdmin(request);

        if (!this.clothService.delete(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", String.format("Cloth with id: %d was not found", id)));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Cloth with id: " + id + " was deleted successfully!",
                "deleted_by", admin.getEmail()
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCloth(@RequestParam(name = "name") String name,
                                         @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                         @RequestParam(defaultValue = "1") @Min(1) int page) {

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Query parameter 'name' cannot be empty"
            ));
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ClothingPageDTO> clothPage = this.clothService.findByQuery(pageable, name);

        return buildPagedResponse(clothPage);
    }

    @GetMapping("/catalog")
    public ResponseEntity<?> getCatalog(@RequestParam(defaultValue = "most-sold") String sort,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                        @RequestParam(defaultValue = "1") @Min(1) int page) {

        Pageable pageable = getPageable(page, size, sort);
        Page<ClothingPageDTO> clothPage = getClothesPage(pageable, type, category);

        return buildPagedResponse(clothPage);
    }

    private Pageable getPageable(int page, int size, String sort) {
        return PageRequest.of(page - 1, size, Sort.by(determineSort(sort)).descending());
    }

    private String determineSort(String sort) {
        return "most-sold".equals(sort) ? "saleCount" : "id";
    }

    private Page<ClothingPageDTO> getClothesPage(Pageable pageable, String type, String category) {
        if (StringUtils.hasText(type) && StringUtils.hasText(category)) {
            return this.clothService.findByTypeAndCategory(pageable, type, category);
        } else if (StringUtils.hasText(type)) {
            return this.clothService.findByType(pageable, type);
        } else if (StringUtils.hasText(category)) {
            return this.clothService.findByCategory(pageable, category);
        } else {
            return this.clothService.getAllPage(pageable);
        }
    }

    private ResponseEntity<?> buildPagedResponse(Page<?> clothPage) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "items_on_page", clothPage.getNumberOfElements(),
                "total_items", clothPage.getTotalElements(),
                "total_pages", clothPage.getTotalPages(),
                "current_page", clothPage.getNumber() + 1,
                "clothes", clothPage.getContent()
        ));
    }
}
