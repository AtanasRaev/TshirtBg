package bg.tshirt.database.dto;

import java.util.List;

public class UserProfileDTO extends UserDTO {
    private String firstName;

    private String lastName;

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
