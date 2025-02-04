package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponseDTO {
    private String status;

    private String message;

    private String accessToken;

    private String refreshToken;

    public UserResponseDTO(String status, String message, String token, String refreshToken) {
        this.status = status;
        this.message = message;
        this.accessToken = token;
        this.refreshToken = refreshToken;
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
}
