package bg.tshirt.database.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String address;

    public UserDTO() {
    }

    public UserDTO(Long id, String email, String address) {
        this.id = id;
        this.email = email;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
