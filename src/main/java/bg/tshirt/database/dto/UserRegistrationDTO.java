package bg.tshirt.database.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 3, message = "First name must be at least 3 characters long")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 3, message = "Last name must be at least 3 characters long")
    private String lastName;

    @NotBlank(message = "Phone number cannot be empty")
    @Size(min = 16, max = 16, message = "Phone number must be 16 characters")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "Region cannot be empty")
    private String region;

    public UserRegistrationDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public @NotBlank(message = "Region cannot be empty") String getRegion() {
        return region;
    }

    public void setRegion(@NotBlank(message = "Region cannot be empty") String region) {
        this.region = region;
    }
}