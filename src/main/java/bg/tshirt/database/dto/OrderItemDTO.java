package bg.tshirt.database.dto;

import bg.tshirt.database.entity.enums.Gender;

public class OrderItemDTO {
    private Long clothId;

    private Gender gender;

    private String size;

    private String sleevesOptions;

    private int quantity;

    public Long getClothId() {
        return clothId;
    }

    public void setClothId(Long clothId) {
        this.clothId = clothId;
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

    public String getSleevesOptions() {
        return sleevesOptions;
    }

    public void setSleevesOptions(String sleevesOptions) {
        this.sleevesOptions = sleevesOptions;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
