package bg.tshirt.service;

import bg.tshirt.database.dto.OrderDTO;
import bg.tshirt.database.dto.OrderPageDTO;
import bg.tshirt.database.dto.OrdersDetailsDTO;
import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    void createOrder(OrderDTO dto, UserDTO userDTO);

    void createOrder(OrderDTO dto);

    Page<OrderPageDTO> getAllOrdersByStatus(Pageable pageable, String status);

    Page<OrderPageDTO> getAllOrders(Pageable pageable);

    OrdersDetailsDTO findOrderById(Long id);

    boolean updateStatus(Long id, String status);

    Page<OrderPageDTO> findOrdersByUser(String userEmail, Pageable pageable);
}
