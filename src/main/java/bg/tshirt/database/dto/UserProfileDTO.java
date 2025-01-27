package bg.tshirt.database.dto;

import java.util.List;

public class UserProfileDTO extends UserDTO {
    private List<OrderPageDTO> orders;

    public List<OrderPageDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderPageDTO> orders) {
        this.orders = orders;
    }
}
