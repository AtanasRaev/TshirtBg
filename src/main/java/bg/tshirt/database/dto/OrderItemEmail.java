package bg.tshirt.database.dto;

public class OrderItemEmail {
    private String name;

    private String path;

    private String model;

    private String size;

    private String quantity;

    private String price;

    public OrderItemEmail(String name, String path, String model, String size, String quantity, String price) {
        this.name = name;
        this.path = path;
        this.model = model;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
