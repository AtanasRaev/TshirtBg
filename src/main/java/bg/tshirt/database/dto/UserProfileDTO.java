package bg.tshirt.database.dto;

import java.util.List;

public class UserProfileDTO extends UserDTO {
    private String firstName;

    private String lastName;

    private String city;

    private String region;

    private String phoneNumber;

    private List<OrderPageDTO> orders;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<OrderPageDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderPageDTO> orders) {
        this.orders = orders;
    }
}
