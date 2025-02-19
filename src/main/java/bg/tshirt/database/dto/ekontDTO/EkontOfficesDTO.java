package bg.tshirt.database.dto.ekontDTO;

public class EkontOfficesDTO {
    private String name;

    private EkontAddress address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EkontAddress getAddress() {
        return address;
    }

    public void setAddress(EkontAddress address) {
        this.address = address;
    }
}
