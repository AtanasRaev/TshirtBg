package bg.tshirt.web;

import bg.tshirt.database.dto.ClothPageDTO;
import bg.tshirt.database.dto.OrderDTO;
import bg.tshirt.database.dto.OrderPageDTO;
import bg.tshirt.database.dto.UserDTO;
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
@RequestMapping("/order")
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
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Order created for user"
            ));
        } catch (UnauthorizedException e) {
            this.orderService.createOrder(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Order created for anonymous user"
            ));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOrders(@RequestParam(name = "status") String status,
                                        @RequestParam(name = "sort", defaultValue = "oldest") String sort,
                                        @RequestParam(defaultValue = "10") @Min(4) @Max(100) int size,
                                        @RequestParam(defaultValue = "1") @Min(1) int page,
                                        HttpServletRequest request) {
        this.userService.validateAdmin(request);

        if (!"oldest".equals(sort) &&
                !"newest".equals(sort)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Sort must be 'oldest', or 'newest'"
            ));
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<OrderPageDTO> orderPage = this.orderService.getAllOrdersByStatus(pageable, status);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "items_on_page", orderPage.getNumberOfElements(),
                "total_items", orderPage.getTotalElements(),
                "total_pages", orderPage.getTotalPages(),
                "current_page", orderPage.getNumber() + 1,
                "orders", orderPage.getContent()
        ));
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction = sort.equals("oldest") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page - 1, size, Sort.by(direction, "createdAt"));
    }
}
