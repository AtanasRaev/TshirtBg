package bg.tshirt.web;

import bg.tshirt.database.dto.OrderDTO;
import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.exceptions.UnauthorizedException;
import bg.tshirt.service.OrderService;
import bg.tshirt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
