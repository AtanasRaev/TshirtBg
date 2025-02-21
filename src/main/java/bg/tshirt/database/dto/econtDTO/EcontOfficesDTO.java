package bg.tshirt.database.dto.econtDTO;

public class EcontOfficesDTO {
    private String name;

    private EcontAddress address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EcontAddress getAddress() {
        return address;
    }

    public void setAddress(EcontAddress address) {
        this.address = address;
    }
}
