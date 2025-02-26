package bg.tshirt.web;

import bg.tshirt.database.dto.OrderDTO;
import bg.tshirt.database.dto.OrderPageDTO;
import bg.tshirt.database.dto.OrdersDetailsDTO;
import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.entity.enums.Role;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.exceptions.UnauthorizedException;
import bg.tshirt.service.OrderService;
import bg.tshirt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    public OrderController(UserService userService,
                           OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO dto, HttpServletRequest request) {
        try {
            UserDTO userDTO = this.userService.validateUser(request);
            this.orderService.createOrder(dto, userDTO);
            return successResponse("Order created for user");
        } catch (UnauthorizedException e) {
            this.orderService.createOrder(dto);
            return successResponse("Order created for anonymous user");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOrders(@RequestParam(name = "status", defaultValue = "all") String status,
                                        @RequestParam(name = "sort", defaultValue = "newest") String sort,
                                        @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                        @RequestParam(defaultValue = "1") @Min(1) int page,
                                        HttpServletRequest request) {
        UserDTO userDTO = this.userService.validateUser(request);

        if (!isValidSortOption(sort)) {
            return badRequestResponse();
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<OrderPageDTO> orderPage = getListOrdersPage(pageable, status, userDTO);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "items_on_page", orderPage.getNumberOfElements(),
                "total_items", orderPage.getTotalElements(),
                "total_pages", orderPage.getTotalPages(),
                "current_page", orderPage.getNumber() + 1,
                "orders", orderPage.getContent()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, HttpServletRequest request) {
        this.userService.validateUser(request);

        validateId(id);

        OrdersDetailsDTO order = this.orderService.findOrderById(id);
        if (order == null) {
            throw new NotFoundException("Order with id: " + id + " was not found");
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "order", order
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id,
                                         @RequestParam(name = "status") String status,
                                         HttpServletRequest request) {
        this.userService.validateAdmin(request);

        validateId(id);
        validateStatus(status);

        if (this.orderService.updateStatus(id, status)) {
            return successResponse("Order status updated");
        } else {
            return ResponseEntity.ok(Map.of("status", "info", "message", "Order status is already the same"));
        }
    }

    private void validateId(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Id must be a positive number");
        }
    }

    private void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status must not be empty");
        }

        if (!"confirm".equalsIgnoreCase(status) && !"reject".equalsIgnoreCase(status) && !"pending".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Status must 'confirm', 'reject' or 'pending'");
        }
    }

    private boolean isValidSortOption(String sort) {
        return "oldest".equals(sort) || "newest".equals(sort);
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction = sort.equals("oldest") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page - 1, size, Sort.by(direction, "createdAt"));
    }

    private Page<OrderPageDTO> getListOrdersPage(Pageable pageable, String status, UserDTO userDTO) {
        if ("all".equalsIgnoreCase(status)) {
            return userDTO.getRoles().contains(Role.ADMIN) || userDTO.getRoles().contains(Role.MODERATOR) ? this.orderService.getAllOrders(pageable) : this.orderService.findOrdersByUser(userDTO.getEmail(), pageable);
        } else {
            return userDTO.getRoles().contains(Role.ADMIN) || userDTO.getRoles().contains(Role.MODERATOR) ? this.orderService.getAllOrdersByStatus(pageable, status) : Page.empty();
        }
    }

    private ResponseEntity<?> successResponse(String message) {
        return ResponseEntity.ok(Map.of("status", "success", "message", message));
    }

    private ResponseEntity<?> badRequestResponse() {
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Sort must be 'oldest' or 'newest'"));
    }
}
