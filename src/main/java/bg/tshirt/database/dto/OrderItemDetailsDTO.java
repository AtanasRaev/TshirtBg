package bg.tshirt.database.dto;

import bg.tshirt.database.entity.enums.Gender;

public class OrderItemDetailsDTO {
    private Long id;

    private String name;

    private String model;

    private Gender gender;

    private String size;

    private String type;

    private int quantity;

    private Double price;

    private Long clothingId;

    public OrderItemDetailsDTO(Long id,
                               String name,
                               String model,
                               Gender gender,
                               String size,
                               String type,
                               int quantity,
                               Double price,
                               Long clothingId) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.gender = gender;
        this.size = size;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.clothingId = clothingId;
    }

    public OrderItemDetailsDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getClothingId() {
        return clothingId;
    }

    public void setClothingId(Long clothingId) {
        this.clothingId = clothingId;
    }
}
