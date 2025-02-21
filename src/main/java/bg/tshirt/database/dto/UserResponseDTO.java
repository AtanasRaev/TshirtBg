package bg.tshirt.database.dto;

import java.util.List;

public class UserResponseDTO {
    private String status;

    private String message;

    private String accessToken;

    private String refreshToken;

    private List<String> roles;

    public UserResponseDTO(String status, String message, String token, String refreshToken, List<String> roles) {
        this.status = status;
        this.message = message;
        this.accessToken = token;
        this.refreshToken = refreshToken;
        this.roles = roles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
