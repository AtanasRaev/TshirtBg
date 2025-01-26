package bg.tshirt.database.entity;

import bg.tshirt.database.entity.enums.Category;
import bg.tshirt.database.entity.enums.Gender;
import bg.tshirt.database.entity.enums.Type;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clothes")
public class Cloth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String model;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "cloth",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REMOVE},
            orphanRemoval = true)
    private List<Image> images;

    public Cloth(String name, String description, double price, String model, Type type, Gender gender, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.model = model;
        this.type = type;
        this.gender = gender;
        this.images = new ArrayList<>();
        this.category = category;
    }

    public Cloth() {
    }

    public String getPageModel(){
        String type = "";

        switch (getType()) {
            case SHORTS -> type = "K";
            case SWEATSHIRT -> type = "SW";
            case LONG_T_SHIRT -> type = "D";
            case KIT -> type = "KT";
        }

        String gender = "";

        switch (getGender()) {
            case MALE -> gender = "M";
            case FEMALE -> gender = "F";
            case CHILD -> gender = "C";
        }

        return String.format("%s%s_%s", getModel(), type, gender);
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
