package bg.tshirt.database.dto;

import bg.tshirt.database.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private String email;

    private String address;

    @JsonIgnore
    private Set<Role> roles;

    @JsonIgnore
    private String password;

    public UserDTO() {
        this.roles = new HashSet<>();
    }

    public UserDTO(String email, String address, Set<Role> roles) {
        this.email = email;
        this.address = address;
        this.roles = roles;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
