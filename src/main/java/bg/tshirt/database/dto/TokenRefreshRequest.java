package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRefreshRequest {
    @JsonProperty("refresh_token")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

