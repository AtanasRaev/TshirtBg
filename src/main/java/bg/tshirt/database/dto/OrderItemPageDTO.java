package bg.tshirt.database.dto;

public class OrderItemPageDTO {
    private Long id;

    private ClothingPageDTO cloth;

    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClothingPageDTO getCloth() {
        return cloth;
    }

    public void setCloth(ClothingPageDTO cloth) {
        this.cloth = cloth;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
