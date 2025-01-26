package bg.tshirt.service;

import bg.tshirt.database.dto.OrderDTO;
import bg.tshirt.database.dto.UserDTO;

public interface OrderService {
    void createOrder(OrderDTO dto, UserDTO userDTO);

    void createOrder(OrderDTO dto);
}
