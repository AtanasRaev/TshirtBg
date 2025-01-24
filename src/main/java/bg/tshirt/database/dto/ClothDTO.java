package bg.tshirt.database.dto;

import bg.tshirt.database.entity.enums.Gender;
import bg.tshirt.database.entity.enums.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ClothDTO {
    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    @NotNull(message = "Price cannot be null.")
    @Positive(message = "Price must be greater than 0.")
    private Double price;

    @NotNull(message = "Type cannot be null.")
    private Type type;

    @NotNull(message = "Gender cannot be null.")
    private Gender gender;

    @NotBlank(message = "Model cannot be blank.")
    private String model;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public @NotBlank(message = "Model cannot be blank.") String getModel() {
        return model;
    }

    public void setModel(@NotBlank(message = "Model cannot be blank.") String model) {
        this.model = model;
    }
}