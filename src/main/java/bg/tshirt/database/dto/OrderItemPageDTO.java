package bg.tshirt.database.dto;

public class OrderItemPageDTO {
    private Long id;

    private ClothPageDTO cloth;

    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClothPageDTO getCloth() {
        return cloth;
    }

    public void setCloth(ClothPageDTO cloth) {
        this.cloth = cloth;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
