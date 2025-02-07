package bg.tshirt.database.dto;

import bg.tshirt.database.entity.enums.Type;

import java.util.List;

public class ClothingPageDTO {
   private Long id;

   private String name;

   private String description;

   private Double price;

   private String model;

   private Type type;

   private List<ImagePageDTO> images;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

    public List<ImagePageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImagePageDTO> images) {
        this.images = images;
    }
}
