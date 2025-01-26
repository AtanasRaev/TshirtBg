package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItemDTO {
    @JsonProperty("cloth_id")
    private Long clothId;

    private int quantity;

    public Long getClothId() {
        return clothId;
    }

    public void setClothId(Long clothId) {
        this.clothId = clothId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
