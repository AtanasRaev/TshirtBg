package bg.tshirt.database.dto;

import bg.tshirt.database.entity.Image;
import bg.tshirt.database.entity.enums.Gender;
import bg.tshirt.database.entity.enums.Type;

import java.util.List;

public class ClothPageDTO {
    private long id;

    private String name;

    private String description;

    private double price;

    private String model;

    private Type type;

    private Gender gender;

    private List<ImagePageDTO> images;

    public ClothPageDTO(long id,
                        String name,
                        String description,
                        double price,
                        String model,
                        Type type,
                        Gender gender) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.model = model;
        this.type = type;
        this.gender = gender;
    }

    public ClothPageDTO() {
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

    public List<ImagePageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImagePageDTO> images) {
        this.images = images;
    }
}
