package bg.tshirt.database.dto;

import bg.tshirt.database.entity.enums.Category;
import bg.tshirt.database.entity.enums.Gender;
import bg.tshirt.database.entity.enums.Type;

import java.util.List;

public class ClothDetailsPageDTO {
    private long id;

    private String name;

    private String description;

    private double price;

    private String model;

    private Type type;

    private Gender gender;

    private Category category;

    private List<ImagePageDTO> images;

    public ClothDetailsPageDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ImagePageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImagePageDTO> images) {
        this.images = images;
    }
}
