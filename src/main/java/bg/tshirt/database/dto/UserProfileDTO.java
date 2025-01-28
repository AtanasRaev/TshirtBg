package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserProfileDTO extends UserDTO {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("second_name")
    private String secondName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private List<OrderPageDTO> orders;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
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
