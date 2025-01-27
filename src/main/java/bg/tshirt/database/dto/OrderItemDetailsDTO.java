package bg.tshirt.database.dto;

public class OrderItemDetailsDTO {
    private Long id;

    private ClothPageDTO cloth;

    private Integer quantity;

    private Double price;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}