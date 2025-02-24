package bg.tshirt.database.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderDTO {
    private String firstName;

    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 16, max = 16, message = "Phone number must be 16 characters")
    private String phoneNumber;

    private String region;

    private String city;

    private String address;

    private String deliveryType;

    private String selectedOffice;

    private Double totalPrice;

    private Double deliveryCost;

    private Double finalPrice;

    private List<OrderItemDTO> cart;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getSelectedOffice() {
        return selectedOffice;
    }

    public void setSelectedOffice(String selectedOffice) {
        this.selectedOffice = selectedOffice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public List<OrderItemDTO> getCart() {
        return cart;
    }

    public void setCart(List<OrderItemDTO> cart) {
        this.cart = cart;
    }
}
