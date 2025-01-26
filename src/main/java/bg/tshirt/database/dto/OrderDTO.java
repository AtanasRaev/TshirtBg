package bg.tshirt.database.dto;

import java.util.List;

public class OrderDTO {
    private String address;

    private List<OrderItemDTO> items;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
